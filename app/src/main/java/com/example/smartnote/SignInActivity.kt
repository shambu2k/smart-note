package com.example.smartnote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartnote.databinding.ActivitySignInBinding
import com.example.smartnote.fragments.SettingsFragment
import com.example.smartnote.helpers.viewLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

  @Inject
  lateinit var mGoogleSignInClient: GoogleSignInClient
  private val binding by viewLifecycle(ActivitySignInBinding::inflate)

  companion object {
    const val RC_SIGN_IN = 1
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    binding.buttonSignIn.setSize(SignInButton.SIZE_WIDE)
    binding.buttonSignIn.setOnClickListener {
      signIn()
    }
  }

  private fun signIn() {
    val signInIntent = mGoogleSignInClient.signInIntent
    startActivityForResult(signInIntent, RC_SIGN_IN)
  }

  override fun onStart() {
    super.onStart()
    val account = GoogleSignIn.getLastSignedInAccount(this)
    if (account != null) {
      startMainActivity()
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
      Toast.makeText(this, "Welcome ${account.displayName}!", Toast.LENGTH_LONG).show()
      startMainActivity()
    } catch (e: ApiException) {
      Log.w(SettingsFragment.TAG, "signInResult:failed code=" + e.statusCode)
    }
  }

  private fun startMainActivity() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
  }
}
