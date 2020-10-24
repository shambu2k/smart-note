package com.example.smartnote.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BookDao {
    @Insert
    suspend fun insertBook(book: Book):Long

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("DELETE FROM user_books_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM user_books_table")
    fun getAllBooks(): LiveData<List<Book>>
}
