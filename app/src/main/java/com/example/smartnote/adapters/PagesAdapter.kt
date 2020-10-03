package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.R

class PagesAdapter: RecyclerView.Adapter<PagesAdapter.PagesViewHolder>() {

    class PagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view_page)
        val textView: TextView = itemView.findViewById(R.id.text_view_page_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.page_item, parent, false)
        return PagesViewHolder(v)
    }

    override fun onBindViewHolder(holder: PagesViewHolder, position: Int) {
        TODO()
    }

    override fun getItemCount(): Int {
        TODO()
    }
}