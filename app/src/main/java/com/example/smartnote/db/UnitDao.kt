package com.example.smartnote.db

import androidx.room.*

@Dao
interface UnitDao {
  @Insert
  suspend fun insertUnitGrid(unitGrid: UnitGrid)

  @Update
  suspend fun updateUnitGrid(unitGrid: UnitGrid)

  @Delete
  suspend fun deleteUnitGrid(unitGrid: UnitGrid)

  @Query("DELETE FROM unit_grid_table")
  suspend fun deleteAllUnitGrid()
}
