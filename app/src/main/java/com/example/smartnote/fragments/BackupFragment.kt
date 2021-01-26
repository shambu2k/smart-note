package com.example.smartnote.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.smartnote.databinding.FragmentBackupBinding
import com.example.smartnote.db.Pdf
import com.example.smartnote.helpers.DriveServiceHelper
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BackupViewModel
import com.example.smartnote.viewmodels.BookViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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
    private lateinit var mPdfs: List<Pdf>

    private val backupViewModel: BackupViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BackupViewModel::class.java)
    }
    private val booksViewModel: BookViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
    }

    companion object {
        const val RC_SIGN_IN = 1
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
        binding.signInButton.setSize(SignInButton.SIZE_STANDARD)
        binding.signInButton.setOnClickListener {
            signIn()
        }
        binding.signOutButton.setOnClickListener {
            signOut()
        }
        binding.uploadButton.setOnClickListener {
            uploadPDF()
        }
        booksViewModel.getAllPDFs().observe(viewLifecycleOwner) {
            mPdfs = it
        }
        backupViewModel.isUploaded.observe(viewLifecycleOwner) { isUploaded ->
            if (isUploaded) {
              Toast.makeText(requireContext(), "Uploaded PDFs!!", Toast.LENGTH_LONG).show()
              backupViewModel.isUploaded.postValue(false)
            }
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun uploadPDF() {
        //backupViewModel.uploadPDF(mDriveServiceHelper, "/data/user/0/com.example.smartnote/files/MyBook/asdasdas/unit1.pdf")
        Toast.makeText(requireContext(), "Uploading...", Toast.LENGTH_LONG).show()
        if (this::mPdfs.isInitialized) {
            backupViewModel.uploadPDFs(mDriveServiceHelper, mPdfs, requireContext().filesDir.toString())
        }
    }

    private fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            updateUI(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            initializeDriveHelper()
            updateUI(account)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
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
            binding.displayImage.visibility = View.VISIBLE
            binding.displayName.visibility = View.VISIBLE
            binding.signOutButton.visibility = View.VISIBLE
            binding.signInButton.visibility = View.INVISIBLE
            binding.uploadButton.visibility = View.VISIBLE
        } else {
            binding.displayImage.visibility = View.INVISIBLE
            binding.displayName.visibility = View.INVISIBLE
            binding.signOutButton.visibility = View.INVISIBLE
            binding.signInButton.visibility = View.VISIBLE
            binding.uploadButton.visibility = View.INVISIBLE
        }
    }
}
