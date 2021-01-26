package com.example.smartnote.helpers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

class UploadWorker(appContext: Context, workerParams: WorkerParameters):
  Worker(appContext, workerParams) {
  override fun doWork(): Result {


    try {
      //calling uploading pdf helper class
      return Result.success()
    }catch (e:Exception){
      return Result.failure()
    }
  }
}
