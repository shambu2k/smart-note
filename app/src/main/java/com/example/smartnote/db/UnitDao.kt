package com.example.smartnote.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query

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
