package com.example.smartnote.db

import javax.inject.Inject

class BookRepository @Inject constructor (private val dao:BookDao) {
    val books = dao.getAllBooks()

    suspend fun insert(book:Book){
        dao.insertBook(book)
    }

    suspend fun update(book: Book){
        dao.updateBook(book)
    }

    suspend fun delete(book: Book){
        dao.deleteBook(book)
    }

    suspend fun deleteAll(){
        dao.deleteAll()
    }
}
