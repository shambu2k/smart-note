package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.R

class SubjectsAdapter: RecyclerView.Adapter<SubjectsAdapter.SubjectsViewHolder>() {

    class SubjectsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_view_subject_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.subject_item, parent, false)
        return SubjectsViewHolder(v)
    }

    override fun onBindViewHolder(holder: SubjectsViewHolder, position: Int) {
        TODO()
    }

    override fun getItemCount(): Int {
        TODO()
    }
}