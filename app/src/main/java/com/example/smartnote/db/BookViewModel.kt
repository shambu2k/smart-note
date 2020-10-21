package com.example.smartnote.db

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BookRepository):ViewModel() {

    val books = repository.books
    val book = MutableLiveData<Book>()

    fun insert(book:Book){
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