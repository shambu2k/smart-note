package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.R

class BooksAdapter: RecyclerView.Adapter<BooksAdapter.BooksViewHolder>() {

    class BooksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_view_book_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false)
        return BooksViewHolder(v)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        TODO()
    }

    override fun getItemCount(): Int {
        TODO()
    }
}