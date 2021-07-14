package com.example.smartnote.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.smartnote.repository.BackupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class BackupViewModel @ViewModelInject constructor(
  private val backupRepository: BackupRepository,
  @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val viewModelJob = SupervisorJob()
  private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)
  val isUploaded = MutableLiveData<Boolean>(false)
}
