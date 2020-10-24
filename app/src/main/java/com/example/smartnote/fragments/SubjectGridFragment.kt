package com.example.smartnote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smartnote.SubjectGrid
import com.example.smartnote.SubjectGridViewModel
import com.example.smartnote.databinding.FragmentSubjectGridBinding
import com.example.smartnote.viewbinding.viewLifecycle

class SubjectGridFragment : Fragment() {
    private var binding by viewLifecycle<FragmentSubjectGridBinding>()
    private val viewModel: SubjectGridViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)).get(SubjectGridViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectGridBinding.inflate(inflater, container, false)
        binding.buttonAddBook.setOnClickListener {
            addBook()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapOf(
            binding.editTextBookName to binding.containerBookName,
            binding.editTextSubjectOne to binding.containerSubjectOne,
            binding.editTextSubjectTwo to binding.containerSubjectTwo,
            binding.editTextSubjectThree to binding.containerSubjectThree,
            binding.editTextSubjectFour to binding.containerSubjectFour,
            binding.editTextSubjectFive to binding.containerSubjectFive
        ).forEach {
            it.key.addTextChangedListener { text ->
                if (text.toString().isNotEmpty()) {
                    it.value.error = null
                }
            }
        }
    }

    private fun addBook() {
        var allOk = true

        val bookName = binding.editTextBookName.text.toString()
        if (bookName.isEmpty()) {
            allOk = false
            binding.containerBookName.error = "Book Name should not be empty"
        }

        val subjectOneName = binding.editTextSubjectOne.text.toString()
        if (subjectOneName.isEmpty()) {
            allOk = false
            binding.containerSubjectOne.error = "Subject One Name should not be empty"
        }

        val subjectTwoName = binding.editTextSubjectTwo.text.toString()
        if (subjectTwoName.isEmpty()) {
            allOk = false
            binding.containerSubjectTwo.error = "Subject Two Name should not be empty"
        }
        val subjectThreeName = binding.editTextSubjectThree.text.toString()
        if (subjectThreeName.isEmpty()) {
            allOk = false
            binding.containerSubjectThree.error = "Subject Three Name should not be empty"
        }

        val subjectFourName = binding.editTextSubjectFour.text.toString()
        if (subjectFourName.isEmpty()) {
            allOk = false
            binding.containerSubjectFour.error = "Subject Four Name should not be empty"
        }

        val subjectFiveName = binding.editTextSubjectFive.text.toString()
        if (subjectFiveName.isEmpty()) {
            allOk = false
            binding.containerSubjectFive.error = "Subject Five Name should not be empty"
        }

        if (allOk) {
            val subjectGrid = SubjectGrid(null, bookName, subjectOneName, subjectTwoName, subjectThreeName,
                subjectFourName, subjectFiveName)
            viewModel.insert(subjectGrid)
            requireActivity().onBackPressed()
        }
    }
}