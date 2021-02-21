package com.example.smartnote

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.smartnote.databinding.ActivityMainBinding
import com.example.smartnote.db.*
import com.example.smartnote.helpers.UploadWorker
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BookViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private val binding by viewLifecycle(ActivityMainBinding::inflate)

  private lateinit var navController: NavController

  private lateinit var bookViewModel: BookViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val view = binding.root
    setContentView(view)
    setupNavigation()
    //Backup();
  }

  private fun setupNavigation() {
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayShowHomeEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    navController = navHostFragment.navController
    NavigationUI.setupActionBarWithNavController(this, navController)
    NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
    NavigationUI.setupWithNavController(binding.sideNavigationDrawer, navController)
    binding.sideNavigationDrawer.setNavigationItemSelectedListener { item: MenuItem ->
      when (item.itemId) {
        R.id.books_item -> {
          navController.navigate(R.id.booksFragment)
          binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        R.id.qrscanner_item -> {
          scanQr()
          binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        R.id.settings_item -> {
          navController.navigate(R.id.settingsFragment)
          binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        R.id.about_item -> {
          navController.navigate(R.id.aboutFragment)
          binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
      }
      true
    }
    val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
    if(account != null){
      val view : View = binding.sideNavigationDrawer.getHeaderView(0)
      view.findViewById<TextView>(R.id.signIn).setText(account.displayName)
    }
  }

  override fun onBackPressed() {
    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
      binding.drawerLayout.closeDrawer(GravityCompat.START)
      return
    }
    super.onBackPressed()
  }

  override fun onSupportNavigateUp(): Boolean {
    return NavigationUI.navigateUp(navController, binding.drawerLayout)
  }

  private fun scanQr() {
    IntentIntegrator(this).initiateScan()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
    if (result != null) {
      if (result.contents == null) {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
      } else {
        if (Patterns.WEB_URL.matcher(result.contents).matches()) {
          // Open URL
          val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(result.contents))
          startActivity(browserIntent)
        } else {
          Toast.makeText(this, "Scan a valid QR", Toast.LENGTH_LONG).show()
          scanQr()
        }
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data)
    }
  }

  private fun Backup(){
    val periodicWorkRequest = PeriodicWorkRequest
      .Builder(UploadWorker::class.java,24,TimeUnit.HOURS)
      .build()
    WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest)
  }
}
