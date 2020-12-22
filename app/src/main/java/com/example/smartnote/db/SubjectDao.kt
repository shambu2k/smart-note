package com.example.smartnote.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SubjectDao {
  @Insert
  suspend fun insertSubjectGrid(subjectGrid: SubjectGrid)

  @Update
  suspend fun updateSubjectGrid(subjectGrid: SubjectGrid)

  @Delete
  suspend fun deleteSubjectGrid(subjectGrid: SubjectGrid)

  @Query("DELETE FROM subject_grid_table")
  suspend fun deleteAllSubjectGrids()

  @Query("SELECT * FROM subject_grid_table ORDER BY bookName ASC")
  fun getAllSubjectGrids(): LiveData<List<SubjectGrid>>
  
  @Query("SELECT * FROM subject_grid_table WHERE bookName = :bookName")
  fun getSubGrid(bookName:String):List<SubjectGrid>
}
