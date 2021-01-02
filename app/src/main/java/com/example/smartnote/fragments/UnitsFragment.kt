package com.example.smartnote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartnote.adapters.UnitsAdapter
import com.example.smartnote.databinding.FragmentUnitsBinding
import com.example.smartnote.helpers.viewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnitsFragment : Fragment() {

  private var binding by viewLifecycle<FragmentUnitsBinding>()
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentUnitsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val args: UnitsFragmentArgs by navArgs()
    val path = args.subjectFolderPath
    val adapter = UnitsAdapter(path)
    binding.unitsRecyclerView.layoutManager = LinearLayoutManager(activity)
    binding.unitsRecyclerView.adapter = adapter
  }
}
