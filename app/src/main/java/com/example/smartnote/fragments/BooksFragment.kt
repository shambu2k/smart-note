package com.example.smartnote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartnote.databinding.FragmentBooksBinding
import com.example.smartnote.helpers.viewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BooksFragment : Fragment() {
    private var binding by viewLifecycle<FragmentBooksBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBooksBinding.inflate(inflater, container, false)
        binding.buttonAddBook.setOnClickListener {
           val action = BooksFragmentDirections.actionBooksFragmentToSubjectGridFragment()
            findNavController().navigate(action)
        }
        return binding.root
    }
}
