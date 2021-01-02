package com.example.smartnote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.databinding.UnitItemBinding
import com.example.smartnote.fragments.UnitsFragmentDirections

class UnitsAdapter(private var subjectPath: String) : RecyclerView.Adapter<UnitsAdapter.UnitsViewHolder>() {
  class UnitsViewHolder(b: UnitItemBinding) : RecyclerView.ViewHolder(b.root) {
    var binding = b
    var unitTextView = binding.textViewUnitName
    fun bind(unitName: String) {
      unitTextView.text = unitName
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitsViewHolder {
    return UnitsViewHolder(UnitItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun getItemCount(): Int = 5

  override fun onBindViewHolder(holder: UnitsViewHolder, position: Int) {
    holder.bind("Unit ${position + 1}")
    holder.itemView.setOnClickListener {
      val unitFolderPath = subjectPath + "/unit${position + 1}"
      it.findNavController().navigate(UnitsFragmentDirections.actionUnitsFragmentToPagesFragment(unitFolderPath))
    }
  }
}
