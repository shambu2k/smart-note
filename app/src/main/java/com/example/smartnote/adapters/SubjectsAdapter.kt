package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.databinding.SubjectItemBinding

class SubjectsAdapter: RecyclerView.Adapter<SubjectsAdapter.SubjectsViewHolder>() {

    class SubjectsViewHolder(b: SubjectItemBinding) : RecyclerView.ViewHolder(b.root) {
        val binding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectsViewHolder {
        return SubjectsViewHolder(SubjectItemBinding.inflate(LayoutInflater.from(parent.context), parent,
            false))
    }

    override fun onBindViewHolder(holder: SubjectsViewHolder, position: Int) {
        TODO()
    }

    override fun getItemCount(): Int {
        TODO()
    }
}