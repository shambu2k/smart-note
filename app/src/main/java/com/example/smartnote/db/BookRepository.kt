package com.example.smartnote.db

import androidx.lifecycle.LiveData
import javax.inject.Inject

class BookRepository @Inject constructor (
  private val bookDao: BookDao,
  private val subjectDao: SubjectDao,
  private val pdfDao: PdfDao
) {
  val books = bookDao.getAllBooks()
  private val pdfs = pdfDao.getAllpdfs()

  suspend fun insertBook(book: Book) {
    bookDao.insertBook(book)
  }

  suspend fun insertSubjectGrid(subjectGrid: SubjectGrid) {
    subjectDao.insertSubjectGrid(subjectGrid)
  }

  fun getBook(id: Int): LiveData<Book> {
    return bookDao.getBook(id)
  }

  fun getBookWithName(name: String): Book {
    return bookDao.getBookWithName(name)
  }

  suspend fun getSubjectGrid(bookName: String): List<SubjectGrid> {
    return subjectDao.getSubGrid(bookName)
  }

  fun getAllPDFs(): LiveData<List<Pdf>> {
    return pdfs
  }

  fun getRecPdfs(): LiveData<List<Pdf>> {
    return pdfDao.getRecentPdfs()
  }

  suspend fun insertPdf(pdf: Pdf) {
    pdfDao.insertPdf(pdf)
  }
  suspend fun deletePdf(name: String) {
    pdfDao.deletePdfByname(name)
  }
}
