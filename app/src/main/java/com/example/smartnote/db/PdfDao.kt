package com.example.smartnote.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PdfDao {

  @Insert
  suspend fun insertPdf(pdf: Pdf)

  @Update
  suspend fun updatePdf(pdf: Pdf)

  @Delete
  suspend fun deletepdf(pdf: Pdf)

  @Query("DELETE FROM pdf_locations_table")
  suspend fun deleteAllpdfs()

  @Query("SELECT * FROM pdf_locations_table")
  fun getAllpdfs(): LiveData<List<Pdf>>

  @Query("DELETE FROM pdf_locations_table where pdf_name=:name")
  suspend fun deletePdfByname(name:String)


}
