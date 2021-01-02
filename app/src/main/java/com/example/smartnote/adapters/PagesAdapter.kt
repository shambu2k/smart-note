package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.databinding.PageItemBinding
import com.squareup.picasso.Picasso
import java.io.File

class PagesAdapter(val listImages: Array<File>) : RecyclerView.Adapter<PagesAdapter.PagesViewHolder>() {

  class PagesViewHolder(b: PageItemBinding) : RecyclerView.ViewHolder(b.root) {
    val binding = b
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesViewHolder {
    return PagesViewHolder(
      PageItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )
  }

  override fun onBindViewHolder(holder: PagesViewHolder, position: Int) {
    Picasso.get().load(File(listImages[position].toURI())).into(holder.binding.imageViewPage)
  }

  override fun getItemCount(): Int {
    return listImages.size
  }
}
