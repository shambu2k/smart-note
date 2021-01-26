package com.example.smartnote.viewmodels

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.smartnote.db.Pdf
import com.example.smartnote.helpers.DriveServiceHelper
import com.example.smartnote.repository.BackupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BackupViewModel @ViewModelInject constructor(
    private val backupRepository: BackupRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)
    val isUploaded = MutableLiveData<Boolean>(false)

    fun uploadPDF(driveServiceHelper: DriveServiceHelper, pdfPath: String) {
        scope.launch {
            backupRepository.uploadPDF(driveServiceHelper, pdfPath)
        }
    }

    fun uploadPDFs(driveServiceHelper: DriveServiceHelper, pdfs: List<Pdf>, basePath: String) {
        isUploaded.postValue(false)
        scope.launch {
            pdfs.forEach {
                Log.i("Backup", "$basePath${it.location}/${it.name}.pdf")
                backupRepository.uploadPDF(driveServiceHelper, "$basePath${it.location}/${it.name}.pdf")
            }
            isUploaded.postValue(true)
        }
    }
}
