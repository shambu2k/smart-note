package com.example.smartnote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartnote.databinding.FragmentPagesBinding
import com.example.smartnote.helpers.viewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PagesFragment : Fragment() {


  private var binding by viewLifecycle<FragmentPagesBinding>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentPagesBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

  }


}
