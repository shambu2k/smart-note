package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.databinding.BookItemBinding

class BooksAdapter: RecyclerView.Adapter<BooksAdapter.BooksViewHolder>() {

    class BooksViewHolder(b: BookItemBinding) : RecyclerView.ViewHolder(b.root) {
        val binding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        return BooksViewHolder(BookItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        TODO()
    }

    override fun getItemCount(): Int {
        TODO()
    }
}