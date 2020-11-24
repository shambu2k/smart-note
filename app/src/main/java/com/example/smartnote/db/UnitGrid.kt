package com.example.smartnote.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unit_grid_table")
data class UnitGrid(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var bookname:String,
    var subjectName: String,
    var unitOne: String,
    var unitTwo: String,
    var unitThree: String,
    var unitFour: String,
    var unitFive: String
)