package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.databinding.BookItemBinding
import com.example.smartnote.db.Book
import com.example.smartnote.fragments.BooksFragmentDirections

class BooksAdapter(private var books: List<Book>) :
  RecyclerView.Adapter<BooksAdapter.BooksViewHolder>() {

  class BooksViewHolder(b: BookItemBinding) : RecyclerView.ViewHolder(b.root) {
    val binding = b
    var bookTextView = binding.textViewBookName
    fun bind(book: Book) {
      bookTextView.text = book.name
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
    return BooksViewHolder(
      BookItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )
  }

  override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
    holder.bind(books[position])
    holder.itemView.setOnClickListener {
      val action = BooksFragmentDirections.actionBooksFragmentToSubjectsFragment(books[position].id,books[position].name)
      Navigation.findNavController(holder.itemView).navigate(action)
    }
  }

  override fun getItemCount(): Int {
    return books.size
  }

  fun refresh(newBooksList: List<Book>) {
    books = newBooksList
    notifyDataSetChanged()
  }
}
