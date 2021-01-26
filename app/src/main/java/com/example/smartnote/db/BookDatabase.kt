package com.example.smartnote.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartnote.helpers.Converter

@Database(entities = [Book::class, SubjectGrid::class, UnitGrid::class, Pdf::class], version = 1)
@TypeConverters(Converter::class)
abstract class BookDatabase : RoomDatabase() {
  abstract val bookDao: BookDao
  abstract val subjectDao: SubjectDao
  abstract val unitDao: UnitDao
  abstract val pdfDao:PdfDao
  companion object {
    @Volatile
    private var INSTANCE: BookDatabase? = null
    fun getInstance(context: Context): BookDatabase {
      synchronized(this) {
        var instance = INSTANCE
        if (instance == null) {
          instance = Room.databaseBuilder(
            context.applicationContext,
            BookDatabase::class.java,
            "user_books_table"
          ).build()
        }
        return instance
      }
    }
  }
}
