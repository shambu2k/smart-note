package com.example.smartnote.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartnote.db.SubjectGrid
import com.example.smartnote.db.SubjectGridRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SubjectGridViewModel @ViewModelInject constructor(
    private val repository: SubjectGridRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val viewModelJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val allSubjectGrids: LiveData<List<SubjectGrid>> = repository.getAllSubjectGrids()

    fun insert(subjectGrid: SubjectGrid) {
        scope.launch {
            repository.insert(subjectGrid)
        }
    }

    fun update(subjectGrid: SubjectGrid) {
        scope.launch {
            repository.update(subjectGrid)
        }
    }

    fun delete(subjectGrid: SubjectGrid) {
        scope.launch {
            repository.delete(subjectGrid)
        }
    }

    fun deleteAllSubjectGrids() {
        scope.launch {
            repository.deleteAllSubjectGrids()
        }
    }

    fun getAllSubjectGrids(): LiveData<List<SubjectGrid>> {
        return allSubjectGrids
    }
}
