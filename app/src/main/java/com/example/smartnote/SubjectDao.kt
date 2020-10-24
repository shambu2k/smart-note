package com.example.smartnote

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SubjectDao {
    @Insert
    suspend fun insert(subjectGrid: SubjectGrid)

    @Update
    suspend fun update(subjectGrid: SubjectGrid)

    @Delete
    suspend fun delete(subjectGrid: SubjectGrid)

    @Query("DELETE FROM subject_grid_table")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM subject_grid_table ORDER BY bookName ASC")
    fun getAllNotes(): LiveData<List<SubjectGrid>>
}