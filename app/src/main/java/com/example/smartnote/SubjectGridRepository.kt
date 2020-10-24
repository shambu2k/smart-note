package com.example.smartnote

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubjectGridRepository(application: Application) {
    private lateinit var subjectDao: SubjectDao
    private lateinit var allSubjectGrids: LiveData<List<SubjectGrid>>

    init {
        val database = "get database"
        //assign subjectDao
        //assign all notes
    }

    suspend fun insert(subjectGrid: SubjectGrid) {
        CoroutineScope(Dispatchers.IO).launch {
            subjectDao.insert(subjectGrid)
        }
    }

    suspend fun update(subjectGrid: SubjectGrid) {
        CoroutineScope(Dispatchers.IO).launch {
            subjectDao.update(subjectGrid)
        }
    }

    suspend fun delete(subjectGrid: SubjectGrid) {
        CoroutineScope(Dispatchers.IO).launch {
            subjectDao.delete(subjectGrid)
        }
    }

    suspend fun deleteAllSubjectGrids() {
        CoroutineScope(Dispatchers.IO).launch {
            subjectDao.deleteAllNotes()
        }
    }

    fun getAllSubjectGrids(): LiveData<List<SubjectGrid>> {
        return allSubjectGrids
    }
}