package com.example.smartnote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.smartnote.adapters.PagesAdapter
import com.example.smartnote.databinding.FragmentPagesBinding
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.FileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PagesFragment : Fragment() {

  private var binding by viewLifecycle<FragmentPagesBinding>()
  private val viewModel: FileViewModel by lazy {
    ViewModelProvider(requireActivity()).get(FileViewModel::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentPagesBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val args: PagesFragmentArgs by navArgs()
    val list = viewModel.getFiles(args.unitFolderPath)

    binding.pagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    val adapter = list?.let { PagesAdapter(it) }
    binding.pagesRecyclerView.adapter = adapter
  }
}
