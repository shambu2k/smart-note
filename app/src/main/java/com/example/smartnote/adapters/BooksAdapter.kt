package com.example.smartnote.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.databinding.BookItemBinding
import com.example.smartnote.db.Book
import com.example.smartnote.fragments.BooksFragmentDirections
import java.util.*


class BooksAdapter(private var books: List<Book>, context:Context) :
  RecyclerView.Adapter<BooksAdapter.BooksViewHolder>() {

  private val sharedPref = "sharedPrefs"
  lateinit var colorStrings : Array<String>

  init {
      val sharedPreferences = context.getSharedPreferences(sharedPref,0)
      val colorString = sharedPreferences?.getString("COLOR","")
      if (colorString != null) {
        colorStrings = colorString.split(",").toTypedArray()
      }
  }

  class BooksViewHolder(b: BookItemBinding) : RecyclerView.ViewHolder(b.root) {
    val binding = b
    var bookImageTextView = binding.textViewImage
    var bookTextView = binding.textViewBookName
    var layout: LinearLayout = binding.linearLayout
    fun bind(book: Book, color : String) {
      (layout.background as GradientDrawable).setColor(Color.parseColor(color))
      var text : String
      text = if(book.name.length > 2){
        book.name.substring(0, 2).toUpperCase(Locale.ROOT)
      }else{
        book.name.toUpperCase(Locale.ROOT)
      }
      bookImageTextView.text = text
      if(book.name.length > 10){
        text = book.name.substring(0,11) + ".."
        bookTextView.text = text
      }else{
        bookTextView.text = book.name
      }
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
    holder.bind(books[position], colorStrings[position+1])
    holder.itemView.setOnClickListener {
      val action = BooksFragmentDirections.actionBooksFragmentToSubjectsFragment(books[position].id, books[position].name)
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
