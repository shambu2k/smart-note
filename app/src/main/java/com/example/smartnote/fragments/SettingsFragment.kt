package com.example.smartnote.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.smartnote.R
import com.example.smartnote.SignInActivity
import com.example.smartnote.UploadService
import com.example.smartnote.databinding.FragmentSettingsBinding
import com.example.smartnote.db.Pdf
import com.example.smartnote.helpers.Constants
import com.example.smartnote.helpers.DriveServiceHelper
import com.example.smartnote.helpers.UploadWorker
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BackupViewModel
import com.example.smartnote.viewmodels.BookViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.Scopes
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.Calendar
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

  private var binding by viewLifecycle<FragmentSettingsBinding>()
  @Inject
  lateinit var mGoogleSignInClient: GoogleSignInClient
  private lateinit var mDriveServiceHelper: DriveServiceHelper
  private lateinit var mPDFs: List<Pdf>
  private lateinit var sharedPreferences: SharedPreferences

  private val backupViewModel: BackupViewModel by lazy {
    ViewModelProvider(requireActivity()).get(BackupViewModel::class.java)
  }
  private val booksViewModel: BookViewModel by lazy {
    ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
  }

  companion object {
    const val TAG = "SettingsFragment"
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentSettingsBinding.inflate(inflater, container, false)
    sharedPreferences = requireActivity().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
    val index = sharedPreferences.getInt("settings", 3)
    binding.radioGrp.check(binding.radioGrp.getChildAt(index).id)
    updateText()
    binding.radioGrp.setOnCheckedChangeListener { group, checkedId ->
      with(sharedPreferences.edit()) {
        putInt("settings", binding.radioGrp.indexOfChild(binding.radioGrp.findViewById(binding.radioGrp.checkedRadioButtonId)))
        apply()
      }
      when (checkedId) {
        R.id.daily -> {
          backup(1)
        }
        R.id.weekly -> {
          backup(7)
        }
        R.id.monthly -> {
          backup(30)
        }
        R.id.none -> {
          backup(0)
        }
        else -> {
          backup(0)
        }
      }
    }
    return binding.root
  }

  private fun updateText() {
    val index = sharedPreferences.getInt("settings", 3)
    var date = Date(sharedPreferences.getLong("UPLOAD_TIME", 0))
    val c = Calendar.getInstance()
    c.time = date
    var isnone = false
    when (index) {
      0 -> {
        c.add(Calendar.DAY_OF_MONTH, 1)
        date = c.time
      }
      1 -> {
        c.add(Calendar.WEEK_OF_MONTH, 1)
        date = c.time
      }
      2 -> {
        c.add(Calendar.MONTH, 1)
        date = c.time
      }
      R.id.none -> {
        isnone = true
      }
      else -> {
        isnone = true
      }
    }
    val text = "Your files will be synced again on " + SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault()).format(date)
    binding.uploadDetails.text = text
    if (isnone) {
      binding.uploadDetails.text = "Please set your choice for syncing the files with drive!"
    }
  }

  override fun onStart() {
    super.onStart()
    val account = GoogleSignIn.getLastSignedInAccount(requireContext())
    if (account != null) {
      initializeDriveHelper()
    }
    updateUI(account)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.signOutButton.setOnClickListener {
      signOut()
    }
    binding.uploadButton.setOnClickListener {
      with(sharedPreferences.edit()) {
        val currentDate = Calendar.getInstance().time.time
        putLong("UPLOAD_TIME", currentDate)
        apply()
      }
      // uploadPdf()
      Toast.makeText(requireContext(), "Uploading...", Toast.LENGTH_LONG).show()

      val intent = Intent(activity, UploadService::class.java)
      activity?.let { it1 -> ContextCompat.startForegroundService(it1, intent) }
      activity?.startService(intent)
      updateText()
    }

    booksViewModel.getAllPDFs().observe(viewLifecycleOwner) {
      mPDFs = it
    }
    backupViewModel.isUploaded.observe(viewLifecycleOwner) { isUploaded ->
      if (isUploaded) {
        Toast.makeText(requireContext(), "Uploaded PDFs!!", Toast.LENGTH_LONG).show()
        backupViewModel.isUploaded.postValue(false)
        val currentDate = Calendar.getInstance().time.time
        with(sharedPreferences.edit()) {
          putLong(Constants.LAST_SYNCED_TIME, currentDate)
          apply()
        }
      }
    }
  }

  private fun signOut() {
    mGoogleSignInClient.signOut().addOnCompleteListener {
      Toast.makeText(requireContext(), "Signed out!", Toast.LENGTH_LONG).show()
      val intent = Intent(requireActivity(), SignInActivity::class.java)
      startActivity(intent)
      requireActivity().finish()
    }
  }

  private fun initializeDriveHelper() {
    val credential = GoogleAccountCredential
      .usingOAuth2(requireContext(), Collections.singleton(Scopes.DRIVE_FILE))
    val account = GoogleSignIn.getLastSignedInAccount(requireContext())
    credential.selectedAccount = account!!.account
    val googleDriveService = Drive.Builder(
      NetHttpTransport(),
      GsonFactory(),
      credential
    ).setApplicationName("Smart Note").build()
    mDriveServiceHelper = DriveServiceHelper(googleDriveService)
  }

  private fun updateUI(account: GoogleSignInAccount?) {
    if (account != null) {
      binding.displayName.text = account.displayName
      binding.displayEmail.text = account.email
      val uri = account.photoUrl
      Picasso.get().load(uri).into(binding.displayImage)
    }
  }

  private fun backup(time: Long) {
    with(sharedPreferences.edit()) {
      val currentDate = Calendar.getInstance().time.time
      putLong("UPLOAD_TIME", currentDate)
      apply()
      updateText()
    }
    context?.let { WorkManager.getInstance(it).cancelAllWork() }
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .setRequiresBatteryNotLow(true)
      .setRequiresStorageNotLow(true)
      .build()
    if (time != 0L) {
      Log.d("TIME", time.toString())
      val period = time * 24 * 60
      val periodicWorkRequest = PeriodicWorkRequest
        .Builder(UploadWorker::class.java, period, TimeUnit.MINUTES, 5, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()
      context?.let { WorkManager.getInstance(it).enqueue(periodicWorkRequest) }
    }
  }
}
