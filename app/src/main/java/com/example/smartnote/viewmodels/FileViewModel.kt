package com.example.smartnote.viewmodels

import android.graphics.Bitmap
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.smartnote.helpers.FileSystemHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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

  fun storeImage(bitmap: Bitmap,fileName:String, filePath: String){
    scope.launch {
      fileSystemHelper.storeImage(bitmap,fileName,filePath)
    }
  }
}
