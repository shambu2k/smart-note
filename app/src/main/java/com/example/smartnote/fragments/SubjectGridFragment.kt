package com.example.smartnote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.smartnote.db.SubjectGrid
import com.example.smartnote.databinding.FragmentSubjectGridBinding
import com.example.smartnote.db.Book
import com.example.smartnote.helpers.FileSystemHelper
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BookViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectGridFragment : Fragment() {
    private var binding by viewLifecycle<FragmentSubjectGridBinding>()
    private val viewModel: BookViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
    }
    private lateinit var fileSystemHelper: FileSystemHelper

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
        fileSystemHelper = FileSystemHelper(requireContext())
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
            binding.containerSubjectOne.error = "Subject One Name should be unique and not empty"
        }

        val subjectTwoName = binding.editTextSubjectTwo.text.toString()
        if (subjectTwoName.isEmpty() || subjectTwoName == subjectOneName) {
            allOk = false
            binding.containerSubjectTwo.error = "Subject Two Name should be unique and not empty"
        }
        val subjectThreeName = binding.editTextSubjectThree.text.toString()
        if (subjectThreeName.isEmpty() || subjectThreeName == subjectTwoName
            || subjectThreeName == subjectOneName) {
            allOk = false
            binding.containerSubjectThree.error = "Subject Three Name should be unique and not empty"
        }

        val subjectFourName = binding.editTextSubjectFour.text.toString()
        if (subjectFourName.isEmpty() || subjectFourName == subjectThreeName
            || subjectFourName == subjectTwoName || subjectFourName == subjectOneName) {
            allOk = false
            binding.containerSubjectFour.error = "Subject Four Name should be unique and not empty"
        }

        val subjectFiveName = binding.editTextSubjectFive.text.toString()
        if (subjectFiveName.isEmpty() || subjectFiveName == subjectFourName
            || subjectFiveName == subjectThreeName || subjectFiveName == subjectTwoName
            || subjectFiveName == subjectOneName) {
            allOk = false
            binding.containerSubjectFive.error = "Subject Five Name should be unique and not empty"
        }

        if (allOk) {
            val subjectGrid = SubjectGrid(null, bookName, subjectOneName, subjectTwoName,
                subjectThreeName, subjectFourName, subjectFiveName)
            val subjects = listOf(subjectOneName, subjectTwoName, subjectThreeName,
                subjectFourName, subjectFiveName)
            val subjectFolderPaths = mutableListOf<String>()
            fileSystemHelper.makeFolder(bookName, "")
            subjects.forEach { subjectName->
                fileSystemHelper.makeFolder(subjectName, "/$bookName")
                subjectFolderPaths.add("/${bookName}/${subjectName}")
            }
            val book = Book(0, bookName, subjects, subjectFolderPaths)
            viewModel.insertSubjectGrid(subjectGrid)
            viewModel.insertBook(book)
            findNavController().navigate(SubjectGridFragmentDirections.actionSubjectGridFragmentToScannerFragment())
        }
    }
}
