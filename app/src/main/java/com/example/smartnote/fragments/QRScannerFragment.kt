package com.example.smartnote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartnote.databinding.FragmentQRScannerBinding
import com.example.smartnote.viewbinding.viewLifecycle

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRScannerFragment : Fragment() {

    private var binding by viewLifecycle<FragmentQRScannerBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQRScannerBinding.inflate(inflater, container, false)
        return binding.root
    }
}
