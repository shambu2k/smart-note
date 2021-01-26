package com.example.smartnote.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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

    fun uploadPDF(driveServiceHelper: DriveServiceHelper, pdfPath: String) {
        scope.launch {
            backupRepository.uploadPDF(driveServiceHelper, pdfPath)
        }
    }
}
