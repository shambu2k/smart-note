package com.example.smartnote.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.smartnote.helpers.FileSystemHelper
import kotlinx.coroutines.*
import java.io.File

class FileViewModel @ViewModelInject constructor(
  private val fileSystemHelper: FileSystemHelper,
  @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
  private val viewModelJob = SupervisorJob()
  private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

  fun makeFolder(folderName: String, filePath: String) {
    scope.launch {
      fileSystemHelper.makeFolder(folderName, filePath)
    }
  }

  fun storeImage(bitmap: Bitmap?, fileName: String, filePath: String) {
    scope.launch {
      if (bitmap != null) {
        fileSystemHelper.storeImage(bitmap, fileName, filePath)
      }
    }
  }
  fun getFiles(folderPath: String): Array<File>? {
    var list: Array<File>? = null
    runBlocking(Dispatchers.IO) {
      try {
        list = fileSystemHelper.getFilesList(folderPath)
        Log.i("info", list.toString())
      } catch (e: Exception) {
        e.stackTrace
      }
    }
    return list
  }

  override fun onCleared() {
    viewModelJob.cancel()
    super.onCleared()
  }
}
