package com.example.smartnote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smartnote.databinding.FragmentBooksBinding
import com.example.smartnote.viewBindingDelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BooksFragment : Fragment() {
    private val binding by viewBinding(FragmentBooksBinding::bind)
}
