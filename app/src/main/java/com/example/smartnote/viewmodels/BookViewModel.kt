package com.example.smartnote.viewmodels

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.smartnote.db.Book
import com.example.smartnote.db.BookRepository
import com.example.smartnote.db.Pdf
import com.example.smartnote.db.SubjectGrid
import kotlinx.coroutines.*

class BookViewModel @ViewModelInject constructor(
  private val bookRepository: BookRepository,
  @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val viewModelJob = SupervisorJob()

  val books = bookRepository.books
  val book = MutableLiveData<Book>()

  private val allSubjectGrids: LiveData<List<SubjectGrid>> = bookRepository.getAllSubjectGrids()

  private val scope = CoroutineScope(Dispatchers.IO + viewModelJob)

  fun insertBook(book: Book) {
    scope.launch {
      bookRepository.insertBook(book)
    }
  }

  fun updateBook(book: Book) {
    scope.launch {
      bookRepository.updateBook(book)
    }
  }
  fun deleteBook(book: Book) {
    scope.launch {
      bookRepository.deleteBook(book)
    }
  }

  fun deleteAllBooks() {
    scope.launch {
      bookRepository.deleteAllBooks()
    }
  }

  fun insertSubjectGrid(subjectGrid: SubjectGrid) {
    scope.launch {
      bookRepository.insertSubjectGrid(subjectGrid)
    }
  }

  fun updateSubjectGrid(subjectGrid: SubjectGrid) {
    scope.launch {
      bookRepository.updateSubjectGrid(subjectGrid)
    }
  }

  fun deleteSubjectGrid(subjectGrid: SubjectGrid) {
    scope.launch {
      bookRepository.deleteSubjectGrid(subjectGrid)
    }
  }

  fun deleteAllSubjectGrids() {
    scope.launch {
      bookRepository.deleteAllSubjectGrids()
    }
  }

  fun getAllSubjectGrids(): LiveData<List<SubjectGrid>> {
    return allSubjectGrids
  }

  override fun onCleared() {
    viewModelJob.cancel()
    super.onCleared()
  }

  fun getBookById(id: Int): LiveData<Book> {
    return bookRepository.getBook(id)
  }

  fun getSubjectFolderPath(bookName: String, subNo: Int): String {
    var sub: List<SubjectGrid>? = null
    runBlocking(Dispatchers.IO) {
      try {
        sub = bookRepository.getSubjectGrid(bookName)
        Log.i("info", sub.toString())
      } catch (e: Exception) {
        Log.d("exc", e.message.toString())
      }
    }
    if (sub == null) {
      return "null"
    } else {
      return when (subNo) {
        1 -> sub!![0].subjectOne
        2 -> sub!![0].subjectTwo
        3 -> sub!![0].subjectThree
        4 -> sub!![0].subjectFour
        5 -> sub!![0].subjectFive
        else -> "invalid"
      }
    }
  }

  fun getUnitFolderPath() {
    // TODO(): unit number can be passed as parameters
  }

  fun getAllPDFs(): LiveData<List<Pdf>> {
    return bookRepository.getAllPDFs()
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
