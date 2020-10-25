package com.example.smartnote.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartnote.db.Book
import com.example.smartnote.db.BookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BookViewModel @ViewModelInject constructor(
    private val repository: BookRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
):ViewModel() {

    private val viewModelJob = SupervisorJob()

    val books = repository.books
    val book = MutableLiveData<Book>()

    private val ViewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    fun insert(book: Book){
        ViewModelScope.launch {
            repository.insert(book)
        }
    }

    fun update(book: Book){
        ViewModelScope.launch {
            repository.update(book)
        }
    }
    fun delete(book: Book){
        ViewModelScope.launch {
            repository.delete(book)
        }
    }

    fun clearAll(){
        ViewModelScope.launch {
            repository.deleteAll()
        }
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}
