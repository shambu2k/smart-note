package com.example.smartnote.helpers

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileSystemHelper(@ApplicationContext var context: Context) {

  suspend fun makeFolder(folderName: String, filePath: String) {
    val medFolder = File(context.filesDir.toString() + filePath, folderName)
    Log.i("myTag", context.filesDir.toString() + filePath)
    if (!medFolder.exists()) {
      medFolder.mkdirs()
    }
  }
  suspend fun storeImage(bitmap: Bitmap, fileName: String, filePath: String) {

    val directory = File(context.filesDir.toString() + filePath, "unit" + fileName)
    if (!directory.exists()) {
      directory.mkdir()
    }
    val timeStamp =
      SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val mypath = File(directory, "IMG_" + timeStamp + ".png")

    var fos: FileOutputStream? = null
    try {
      fos = FileOutputStream(mypath)
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
      fos.close()
    } catch (e: Exception) {
      Log.e("SAVE_IMAGE", e.message, e)
    }
  }

  suspend fun getFilesList(folderPath: String): Array<File>? {
    val path = File(context.filesDir.toString() + folderPath)
    return path.listFiles()
  }
}
