package com.example.smartnote.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.smartnote.AlertReceiver
import com.example.smartnote.R
import com.example.smartnote.SignInActivity
import com.example.smartnote.UploadService
import com.example.smartnote.databinding.FragmentSettingsBinding
import com.example.smartnote.db.Pdf
import com.example.smartnote.helpers.Constants
import com.example.smartnote.helpers.DriveServiceHelper
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
import java.util.*
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
    binding.radioGrp.setOnCheckedChangeListener { group, checkedId ->
      when (checkedId) {
        R.id.daily -> {
          backupAlarm(1)
        }
        R.id.weekly -> {
          backupAlarm(7)
        }
        R.id.monthly -> {
          backupAlarm(30)
        }
        R.id.none -> {
          backupAlarm(0)
        }
        else -> {
          backupAlarm(0)
        }
      }
      with(sharedPreferences.edit()) {
        putInt("settings", binding.radioGrp.indexOfChild(binding.radioGrp.findViewById(binding.radioGrp.checkedRadioButtonId)))
        apply()
      }
    }
    return binding.root
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
      Toast.makeText(requireContext(), "Uploading...", Toast.LENGTH_LONG).show()

      val intent = Intent(activity, UploadService::class.java)
      activity?.let { it1 -> ContextCompat.startForegroundService(it1, intent) }
      activity?.startService(intent)
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

  private fun backupAlarm(time: Long) {
    cancelAlarm()
    if (time == 0L) {
      return
    }
    val minutes = time*24*60
    sharedPreferences.edit {
      putLong("backup_period", minutes)
      apply()
    }
    val c = Calendar.getInstance()
    c.add(Calendar.MINUTE, minutes.toInt())
    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(requireContext(), AlertReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, 0)
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
  }

  private fun cancelAlarm() {
    val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(requireContext(), AlertReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, 0)
    alarmManager.cancel(pendingIntent);
  }
}
