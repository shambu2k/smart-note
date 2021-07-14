package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.databinding.RecentItemBinding
import com.example.smartnote.db.Pdf
import com.example.smartnote.fragments.BooksFragmentDirections
import com.squareup.picasso.Picasso
import java.io.File

class RecentPdfsAdapter(private var pdfs: List<Pdf>, private var images: List<String>) :
  RecyclerView.Adapter<RecentPdfsAdapter.PdfsViewHolder>() {

  class PdfsViewHolder(b: RecentItemBinding) : RecyclerView.ViewHolder(b.root) {
    val binding = b
    var bookName = binding.recentsBook
    private var subName = binding.recentsSub
    private var unitName = binding.recentsUnit
    fun bind(pdf: Pdf) {
      val pdfname = pdf.location
      val names = pdfname.split("/")
      bookName.text = names[1]
      subName.text = names[2]
      unitName.text = names[3]
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfsViewHolder {
    return PdfsViewHolder(
      RecentItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )
  }

  override fun getItemCount(): Int {
    return pdfs.size
  }

  override fun onBindViewHolder(holder: PdfsViewHolder, position: Int) {
    holder.bind(pdfs[position])
    Picasso.get().load(File(images[position])).into(holder.binding.pdfImage)
    val pdfname = pdfs[position].location
    val names = pdfname.split("/")
    val unitNo = names[3][names[3].length - 1] - '0'
    holder.itemView.setOnClickListener {
      it.findNavController().navigate(BooksFragmentDirections.actionBooksFragmentToPagesFragment(pdfname, unitNo, names[2], names[1]))
    }
  }

  fun refresh(recentPdfs: List<Pdf>, recimages: List<String>) {
    pdfs = recentPdfs
    images = recimages
    notifyDataSetChanged()
  }
}
