package com.example.smartnote.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.smartnote.SignInActivity
import com.example.smartnote.databinding.FragmentBackupBinding
import com.example.smartnote.db.Pdf
import com.example.smartnote.helpers.Constants.LAST_SYNCED_TIME
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
class BackupFragment : Fragment() {
    private var binding by viewLifecycle<FragmentBackupBinding>()
    @Inject lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mDriveServiceHelper: DriveServiceHelper
    private lateinit var mPDFs: List<Pdf>

    private val backupViewModel: BackupViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BackupViewModel::class.java)
    }
    private val booksViewModel: BookViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
    }

    companion object {
        const val TAG = "BackupFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBackupBinding.inflate(inflater, container, false)
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
            uploadPDF()
        }
        booksViewModel.getAllPDFs().observe(viewLifecycleOwner) {
            mPDFs = it
        }
        backupViewModel.isUploaded.observe(viewLifecycleOwner) { isUploaded ->
            if (isUploaded) {
              Toast.makeText(requireContext(), "Uploaded PDFs!!", Toast.LENGTH_LONG).show()
              backupViewModel.isUploaded.postValue(false)
              val currentDate = Calendar.getInstance().time.time
              val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
              with (sharedPreferences.edit()) {
                putLong(LAST_SYNCED_TIME, currentDate)
                apply()
              }
            }
        }
    }

    private fun uploadPDF() {
        Toast.makeText(requireContext(), "Uploading...", Toast.LENGTH_LONG).show()
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val lastSyncedDate = Date(sharedPreferences.getLong(LAST_SYNCED_TIME, 0))
        if (this::mPDFs.isInitialized) {
            backupViewModel.uploadPDFs(mDriveServiceHelper, mPDFs, requireContext().filesDir.toString(), lastSyncedDate)
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
            val uri = account.photoUrl
            Picasso.get().load(uri).into(binding.displayImage)
        }
    }
}
