package com.example.smartnote.helpers

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FileSystemHelper(@ApplicationContext var context: Context) {

     fun makeFolder(folderName: String,filePath: String){
        val medFolder = File(context.filesDir.toString() + filePath, folderName)
         Log.i("myTag",context.filesDir.toString()+ filePath)
         if (!medFolder.exists()) {
             medFolder.mkdirs()
         }

    }

    fun storeImage(bitmap: Bitmap, fileName: String, filePath: String) {
        val file = File(context.filesDir.toString() + filePath, "$fileName.png")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        val fileOutputStream = FileOutputStream(file)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        fileOutputStream.write(byteArrayOutputStream.toByteArray())
        fileOutputStream.close()
    }
}
