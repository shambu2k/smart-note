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
import com.example.smartnote.db.DbModule.provideBookDao
import com.example.smartnote.db.DbModule.providePdfDao
import com.example.smartnote.db.Pdf
import com.example.smartnote.helpers.Constants
import com.example.smartnote.helpers.DriveServiceHelper
import com.example.smartnote.helpers.PdfHelper
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
import kotlin.collections.ArrayList

class UploadService : Service() {

  private lateinit var mDriveServiceHelper: DriveServiceHelper
  private lateinit var backupRepository: BackupRepository
  private lateinit var pdfs: ArrayList<Pdf>
  private lateinit var basePath: String
  private lateinit var notif: Notification
  private lateinit var pdfHelper: PdfHelper
  //val fileStrings = mutableListOf<String>()

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    startForeground(1, notification)
    val sharedPreferences = this.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
    val lastSyncedDate = Date(sharedPreferences.getLong(Constants.LAST_SYNCED_TIME, 0))
    var isuploaded: Boolean = false
    basePath = applicationContext.filesDir.toString()
    backupRepository = BackupRepository()
    pdfs = ArrayList()
    pdfHelper = PdfHelper()
    CoroutineScope(Dispatchers.IO).launch {
      initializeDriveHelper()
      val books = provideBookDao(applicationContext).getBooks()
      for (book in books) {
        val subjectPaths = book.subjectFolderPaths
        for (subjectPath in subjectPaths) {
          for (i in 1..5) {
            val unitPath = subjectPath + "/unit$i"
            /*val pdf = providePdfDao(applicationContext).getPdfByName(pdfName)
            try to append images to the existing pdf*/
            val images = pdfHelper.getFiles(unitPath, applicationContext)
            if (images != null) {
              val fileStrings = mutableListOf<String>()
              var flag:Int=0
              for (image in images) {
                if (image.name.endsWith(".jpeg")) {
                  fileStrings.add(image.path)
                  if (Date(image.lastModified()).after(lastSyncedDate)) {
                    flag=1;
                  }
                }
              }
              if(flag==1) {
                storePdf(unitPath, fileStrings)
                Log.d("path - service", unitPath)
                val pdfName = unitPath.split('/').toString()
                providePdfDao(applicationContext).deletePdfByname(pdfName)
                val pdf = Pdf(
                  0,
                  unitPath.split('/').toString(),
                  unitPath,
                  Calendar.getInstance().time
                )
                providePdfDao(applicationContext).insertPdf(pdf)
                pdfs.add(pdf)
              }
            }
          }
        }
      }
      val totalPdfs = pdfs.size
      val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
      pdfs.forEachIndexed { index, pdf ->
        if (pdf.time.after(lastSyncedDate)) {
          notif =
            NotificationCompat.Builder(baseContext, "upload_channel")
              .setContentTitle("Uploading pdf : $index out of $totalPdfs")
              .setProgress(totalPdfs, index, false)
              .setSmallIcon(R.drawable.ic_baseline_backup_24)
              .setOngoing(true)
              .build()
          notificationManager.notify(1, notif)
          Log.i("Backup", "$basePath${pdf.location}/${pdf.name}.pdf")
          backupRepository.uploadPDF(mDriveServiceHelper, "$basePath${pdf.location}/${pdf.name}.pdf")
          isuploaded = true
        }
      }
      if (isuploaded) {
        val currentDate = Calendar.getInstance().time.time
        with(sharedPreferences.edit()) {
          putLong(Constants.LAST_SYNCED_TIME, currentDate)
          apply()
        }
      }
      stopForeground(true)
      stopSelf()
      with(sharedPreferences.edit()){
        val currentDate = Calendar.getInstance().time.time
        putLong("UPLOAD_TIME",currentDate)
        apply()
      }
      notif =
        NotificationCompat.Builder(baseContext, "upload_channel")
          .setContentTitle("Synced your files")
          .setSmallIcon(R.drawable.ic_baseline_backup_24)
          .build()
      notificationManager.notify(1, notif)
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

  private fun storePdf(unitPath: String,fileStrings: MutableList<String>) {
    pdfHelper.storePdf(
      fileStrings,
      applicationContext.filesDir.toString() + unitPath,
      unitPath.split('/').toString()
    )
  }
}
