package com.example.smartnote.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartnote.R
import com.example.smartnote.databinding.FragmentLicensesBinding
import com.example.smartnote.helpers.viewLifecycle

class LicensesFragment : Fragment() {

  private var binding by viewLifecycle<FragmentLicensesBinding>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentLicensesBinding.inflate(inflater, container, false)
    return binding.root
  }
}
