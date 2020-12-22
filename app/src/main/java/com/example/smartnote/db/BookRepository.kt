package com.example.smartnote.db

import androidx.lifecycle.LiveData
import javax.inject.Inject

class BookRepository @Inject constructor (
  private val bookDao: BookDao,
  private val subjectDao: SubjectDao,
  private val unitDao: UnitDao
) {
  val books = bookDao.getAllBooks()

  private var allSubjectGrids: LiveData<List<SubjectGrid>> = subjectDao.getAllSubjectGrids()

  suspend fun insertBook(book: Book) {
    bookDao.insertBook(book)
  }

  suspend fun updateBook(book: Book) {
    bookDao.updateBook(book)
  }

  suspend fun deleteBook(book: Book) {
    bookDao.deleteBook(book)
  }

  suspend fun deleteAllBooks() {
    bookDao.deleteAllBooks()
  }

  suspend fun insertSubjectGrid(subjectGrid: SubjectGrid) {
    subjectDao.insertSubjectGrid(subjectGrid)
  }

  suspend fun updateSubjectGrid(subjectGrid: SubjectGrid) {
    subjectDao.updateSubjectGrid(subjectGrid)
  }

  suspend fun deleteSubjectGrid(subjectGrid: SubjectGrid) {
    subjectDao.deleteSubjectGrid(subjectGrid)
  }

  suspend fun deleteAllSubjectGrids() {
    subjectDao.deleteAllSubjectGrids()
  }

  fun getAllSubjectGrids(): LiveData<List<SubjectGrid>> {
    return allSubjectGrids
  }

  fun getBook(id: Int): LiveData<Book> {
    return bookDao.getBook(id)
  }
  fun getSubjectGrid(bookName:String) : List<SubjectGrid>{
    return subjectDao.getSubGrid(bookName)
  }

}
