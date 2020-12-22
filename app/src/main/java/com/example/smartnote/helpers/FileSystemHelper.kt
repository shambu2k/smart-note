package com.example.smartnote.helpers

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class FileSystemHelper(@ApplicationContext var context: Context) {

  suspend fun makeFolder(folderName: String, filePath: String) {
    val medFolder = File(context.filesDir.toString() + filePath, folderName)
    Log.i("myTag", context.filesDir.toString() + filePath)
    if (!medFolder.exists()) {
      medFolder.mkdirs()
    }
  }
  suspend fun storeImage(bitmap: Bitmap, fileName: String, filePath: String) {
   val file = File(context.filesDir.toString() + filePath, "$fileName.png")
    if (!file.exists()) {
      file.mkdir()
    }
    file.createNewFile()
    val fileOutputStream = FileOutputStream(file)
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    fileOutputStream.write(byteArrayOutputStream.toByteArray())
    fileOutputStream.close()
   /* val cw =
      ContextWrapper(context)
    val directory = cw.getDir("files/", Context.MODE_PRIVATE)
    if (!directory.exists()) {
      directory.mkdir()
    }*/
   /* val directory = File(context.filesDir.toString() + filePath,"unit"+fileName)
    if(!directory.exists()){
      directory.mkdir()
    }
    val mypath = File(directory, "image.png")

    var fos: FileOutputStream? = null
    try {
      fos = FileOutputStream(mypath)
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
      fos.close()
    } catch (e: Exception) {
      Log.e("SAVE_IMAGE", e.message, e)
    }*/
  }
}
