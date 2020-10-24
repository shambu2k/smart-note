package com.example.smartnote.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subject_grid_table")
data class SubjectGrid(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var bookName: String,
    var subjectOne: String,
    var subjectTwo: String,
    var subjectThree: String,
    var subjectFour: String,
    var subjectFive: String
)
