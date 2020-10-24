package com.example.smartnote

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SubjectGridViewModel(application: Application): ViewModel() {

    private val repository: SubjectGridRepository = SubjectGridRepository(application)
    private val allSubjectGrids: LiveData<List<SubjectGrid>> = repository.getAllSubjectGrids()

    fun insert(subjectGrid: SubjectGrid) {
        viewModelScope.launch {
            repository.insert(subjectGrid)
        }
    }

    fun update(subjectGrid: SubjectGrid) {
        viewModelScope.launch {
            repository.update(subjectGrid)
        }
    }

    fun delete(subjectGrid: SubjectGrid) {
        viewModelScope.launch {
            repository.delete(subjectGrid)
        }
    }

    fun deleteAllSubjectGrids() {
        viewModelScope.launch {
            repository.deleteAllSubjectGrids()
        }
    }

    fun getAllSubjectGrids(): LiveData<List<SubjectGrid>> {
        return allSubjectGrids
    }
}