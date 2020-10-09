package com.example.smartnote.helpers

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File

class FileSystemHelper(@ApplicationContext var context: Context) {

     fun makeFolder(folderName: String,filePath: String){
        val medFolder = File(context.filesDir.toString() + filePath, folderName)
         Log.i("myTag",context.filesDir.toString()+ filePath)
         if (!medFolder.exists()) {
             medFolder.mkdirs()
         }

    }
}


