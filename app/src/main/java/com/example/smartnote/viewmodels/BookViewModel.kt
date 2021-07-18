package com.example.smartnote.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartnote.db.Book
import com.example.smartnote.db.BookRepository
import com.example.smartnote.db.Pdf
import com.example.smartnote.db.SubjectGrid
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BookViewModel @ViewModelInject constructor(
  private val bookRepository: BookRepository
) : ViewModel() {

  private val viewModelJob = SupervisorJob()

  val books = bookRepository.books
  val book = MutableLiveData<Book>()

  private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

  fun insertBook(book: Book) {
    scope.launch {
      bookRepository.insertBook(book)
    }
  }

  fun insertSubjectGrid(subjectGrid: SubjectGrid) {
    scope.launch {
      bookRepository.insertSubjectGrid(subjectGrid)
    }
  }

  override fun onCleared() {
    viewModelJob.cancel()
    super.onCleared()
  }

  fun getBookById(id: Int): LiveData<Book> {
    return bookRepository.getBook(id)
  }

  fun getSubjectNumber(bookName: String, subjectName: String): Int {
    var book: Book?
    runBlocking(Dispatchers.IO) {
      book = bookRepository.getBookWithName(bookName)
    }
    if (book == null) {
      return 0
    }
    return book!!.subjects.indexOf(subjectName) + 1
  }

  fun getSubjectFolderPath(bookName: String, subNo: Int): String {
    var sub: List<SubjectGrid>? = null
    runBlocking(Dispatchers.IO) {
      try {
        sub = bookRepository.getSubjectGrid(bookName)
      } catch (e: Exception) {
      }
    }
    return if (sub == null) {
      "null"
    } else {
      when (subNo) {
        1 -> sub!![0].subjectOne
        2 -> sub!![0].subjectTwo
        3 -> sub!![0].subjectThree
        4 -> sub!![0].subjectFour
        5 -> sub!![0].subjectFive
        else -> "invalid"
      }
    }
  }

  fun getAllPDFs(): LiveData<List<Pdf>> {
    return bookRepository.getAllPDFs()
  }
  fun getRecPdfs(): LiveData<List<Pdf>> {
    return bookRepository.getRecPdfs()
  }

  fun insertPdf(pdf: Pdf) {
    scope.launch {
      bookRepository.insertPdf(pdf)
    }
  }

  fun deletePdfByName(name: String) {
    scope.launch {
      bookRepository.deletePdf(name)
    }
  }
}
