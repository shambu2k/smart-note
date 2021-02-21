package com.example.smartnote.helpers

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.smartnote.UploadService
import java.lang.Exception

class UploadWorker(appContext: Context, workerParams: WorkerParameters):
  Worker(appContext, workerParams) {
  override fun doWork(): Result {


    try {
      //calling uploading pdf helper class
      val intent = Intent(applicationContext, UploadService::class.java)
      applicationContext.let { it1 -> ContextCompat.startForegroundService(it1,intent) }
      applicationContext.startService(intent)
      return Result.success()
    }catch (e:Exception){
      return Result.failure()
    }
  }
}
