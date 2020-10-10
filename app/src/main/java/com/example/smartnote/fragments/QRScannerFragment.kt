package com.example.smartnote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartnote.databinding.FragmentQRScannerBinding
import com.example.smartnote.viewBindingDelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRScannerFragment : Fragment() {

    private val binding by viewBinding(FragmentQRScannerBinding::bind)
}
