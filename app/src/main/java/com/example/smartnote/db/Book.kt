package com.example.smartnote.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_books_table")
data class Book(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "book_id")
    val id:Int,
    @ColumnInfo(name = "book_name")
    val name:String,
    @ColumnInfo(name = "subject_list")
    val subjects:List<String>,
    @ColumnInfo(name = "subject_path_list")
    val subjectFolderPaths:List<String>

) {
}
