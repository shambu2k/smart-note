package com.example.smartnote.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartnote.db.Book
import com.example.smartnote.db.BookRepository
import kotlinx.coroutines.launch

class BookViewModel @ViewModelInject constructor(
    private val repository: BookRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
):ViewModel() {

    val books = repository.books
    val book = MutableLiveData<Book>()

    fun insert(book: Book){
        viewModelScope.launch {
            repository.insert(book)
        }
    }

    fun update(book: Book){
        viewModelScope.launch {
            repository.update(book)
        }
    }
    fun delete(book: Book){
        viewModelScope.launch {
            repository.delete(book)
        }
    }

    fun clearAll(){
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
