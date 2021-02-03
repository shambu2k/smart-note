package com.example.smartnote

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smartnote.db.DbModule.providePdfDao
import com.example.smartnote.db.Pdf
import com.example.smartnote.helpers.Constants
import com.example.smartnote.helpers.DriveServiceHelper
import com.example.smartnote.repository.BackupRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class UploadService : Service() {

  private lateinit var mDriveServiceHelper: DriveServiceHelper
  private lateinit var backupRepository: BackupRepository
  private var pdfs: List<Pdf>? = null
  private lateinit var basePath: String
  private lateinit var notif: Notification

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    startForeground(1, notification)
    val sharedPreferences = this.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
    val lastSyncedDate = Date(sharedPreferences.getLong(Constants.LAST_SYNCED_TIME, 0))
    basePath = applicationContext.filesDir.toString()
    backupRepository = BackupRepository()
    CoroutineScope(Dispatchers.IO).launch {
      initializeDriveHelper()
      val pdfDao = providePdfDao(applicationContext)
      pdfs = pdfDao.getPdfs()
      val totalPdfs = pdfs?.size ?: 0
      pdfs?.forEachIndexed { index, pdf ->
        if (pdf.time.after(lastSyncedDate)) {
          notif =
            NotificationCompat.Builder(baseContext, "upload_channel")
              .setContentTitle("Uploading pdf : $index out of $totalPdfs")
              .setProgress(totalPdfs, index, false)
              .setSmallIcon(R.drawable.ic_baseline_backup_24)
              .setOngoing(true)
              .build()
          val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
          notificationManager.notify(1, notif)
          Log.i("Backup", "$basePath${pdf.location}/${pdf.name}.pdf")
          backupRepository.uploadPDF(mDriveServiceHelper, "$basePath${pdf.location}/${pdf.name}.pdf")
        }
      }
      val currentDate = Calendar.getInstance().time.time
      with(sharedPreferences.edit()) {
        putLong(Constants.LAST_SYNCED_TIME, currentDate)
        apply()
      }
      stopForeground(true)
      stopSelf()
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    return START_NOT_STICKY
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationChannel = NotificationChannel("upload_channel", "Upload", NotificationManager.IMPORTANCE_DEFAULT)
      val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(notificationChannel)
    }
  }

  private fun initializeDriveHelper() {
    val credential = GoogleAccountCredential
      .usingOAuth2(applicationContext, Collections.singleton(Scopes.DRIVE_FILE))
    val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
    credential.selectedAccount = account!!.account
    val googleDriveService = Drive.Builder(
      NetHttpTransport(),
      GsonFactory(),
      credential
    ).setApplicationName("Smart Note").build()
    mDriveServiceHelper = DriveServiceHelper(googleDriveService)
  }

  private val notification by lazy {
    NotificationCompat.Builder(this, "upload_channel")
      .setSmallIcon(R.drawable.ic_baseline_backup_24)
      .setContentTitle("Uploading pdfs..")
      .build()
  }
}
