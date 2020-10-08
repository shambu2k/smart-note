package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.databinding.PageItemBinding

class PagesAdapter: RecyclerView.Adapter<PagesAdapter.PagesViewHolder>() {

    class PagesViewHolder(b: PageItemBinding) : RecyclerView.ViewHolder(b.root) {
        val binding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesViewHolder {
        return PagesViewHolder(PageItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PagesViewHolder, position: Int) {
        TODO()
    }

    override fun getItemCount(): Int {
        TODO()
    }
}