package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.R
import com.example.smartnote.databinding.SubjectItemBinding
import com.example.smartnote.db.Book

class SubjectsAdapter(private var book: Book) : RecyclerView.Adapter<SubjectsAdapter.SubjectsViewHolder>() {

  class SubjectsViewHolder(b: SubjectItemBinding) : RecyclerView.ViewHolder(b.root) {
    val binding = b
    var subjectTextView = binding.textViewSubjectName
    fun bind(subject: String) {
      subjectTextView.text = subject
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectsViewHolder {
    return SubjectsViewHolder(
      SubjectItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )
  }

  override fun onBindViewHolder(holder: SubjectsViewHolder, position: Int) {
    book.subjects.get(position).let { holder.bind(it) }
    holder.itemView.setOnClickListener {
      // TODO() - navigate to display pages activity
      it.findNavController().navigate(R.id.action_subjectsFragment_to_scannerFragment)
    }
  }

  override fun getItemCount(): Int {
    return book.subjects.size
  }

  fun refresh(book: Book) {
    this.book = book
    notifyDataSetChanged()
  }
}
