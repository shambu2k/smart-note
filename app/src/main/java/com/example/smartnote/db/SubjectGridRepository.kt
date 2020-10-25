package com.example.smartnote.db

import androidx.lifecycle.LiveData
import javax.inject.Inject

class SubjectGridRepository @Inject constructor (private val subjectDao: SubjectDao) {
    private var allSubjectGrids: LiveData<List<SubjectGrid>> = subjectDao.getAllNotes()

    suspend fun insert(subjectGrid: SubjectGrid) {
            subjectDao.insert(subjectGrid)
    }

    suspend fun update(subjectGrid: SubjectGrid) {
            subjectDao.update(subjectGrid)
    }

    suspend fun delete(subjectGrid: SubjectGrid) {
            subjectDao.delete(subjectGrid)
    }

    suspend fun deleteAllSubjectGrids() {
            subjectDao.deleteAllNotes()
    }

    fun getAllSubjectGrids(): LiveData<List<SubjectGrid>> {
        return allSubjectGrids
    }
}
