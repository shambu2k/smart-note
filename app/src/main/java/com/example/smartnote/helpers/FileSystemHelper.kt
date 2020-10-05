package com.example.smartnote.helpers

import android.content.Context
import android.util.Log
import java.io.File

class FileSystemHelper(var context: Context) {

     fun makeFolder(folderName: String,filePath: String){
        val medFolder = File(context.filesDir.toString() + filePath, folderName)
         Log.i("myTag",context.filesDir.toString()+ filePath)
         if (!medFolder.exists()) {
             medFolder.mkdirs()
         }

    }
}


