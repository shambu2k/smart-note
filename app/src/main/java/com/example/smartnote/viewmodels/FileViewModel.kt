package com.example.smartnote.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.smartnote.helpers.FileSystemHelper
import com.example.smartnote.helpers.PdfHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel @ViewModelInject constructor(
  private val fileSystemHelper: FileSystemHelper,
  private val pdfHelper: PdfHelper,
  @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
  private val viewModelJob = SupervisorJob()
  private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)
  val isDeleted = MutableLiveData<Boolean>(false)
  val isStored = MutableLiveData<Boolean>(false)

  fun makeFolder(folderName: String, filePath: String) {
    scope.launch {
      fileSystemHelper.makeFolder(folderName, filePath)
    }
  }

  fun storeImage(bitmap: Bitmap?, fileName: String, filePath: String) {
    scope.launch {
      if (bitmap != null) {
        fileSystemHelper.storeImage(bitmap, fileName, filePath)
        isStored.postValue(true)
      }
    }
  }
  fun getFiles(folderPath: String, context: Context): Array<File>? {
    return pdfHelper.getFiles(folderPath, context)
  }
  fun storePdf(paths: List<String>, outPath: String, fileName: String) {
    pdfHelper.storePdf(paths, outPath, fileName)
  }

  fun deleteFile(fileName: String) {
    scope.launch {
      fileSystemHelper.deleteFile(fileName)
      isDeleted.postValue(true)
    }
  }

  override fun onCleared() {
    viewModelJob.cancel()
    super.onCleared()
  }
}
