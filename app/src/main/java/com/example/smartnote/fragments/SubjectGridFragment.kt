package com.example.smartnote.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.smartnote.R
import com.example.smartnote.databinding.FragmentSubjectGridBinding
import com.example.smartnote.db.Book
import com.example.smartnote.db.SubjectGrid
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BookViewModel
import com.example.smartnote.viewmodels.FileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectGridFragment : Fragment() {
  private var binding by viewLifecycle<FragmentSubjectGridBinding>()
  private val viewModel: BookViewModel by lazy {
    ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
  }

  private lateinit var fileViewModel: FileViewModel

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

    fileViewModel = ViewModelProvider(requireActivity()).get(FileViewModel::class.java)
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
    if (subjectThreeName.isEmpty() || subjectThreeName == subjectTwoName ||
      subjectThreeName == subjectOneName
    ) {
      allOk = false
      binding.containerSubjectThree.error = "Subject Three Name should be unique and not empty"
    }

    val subjectFourName = binding.editTextSubjectFour.text.toString()
    if (subjectFourName.isEmpty() || subjectFourName == subjectThreeName ||
      subjectFourName == subjectTwoName || subjectFourName == subjectOneName
    ) {
      allOk = false
      binding.containerSubjectFour.error = "Subject Four Name should be unique and not empty"
    }

    val subjectFiveName = binding.editTextSubjectFive.text.toString()
    if (subjectFiveName.isEmpty() || subjectFiveName == subjectFourName ||
      subjectFiveName == subjectThreeName || subjectFiveName == subjectTwoName ||
      subjectFiveName == subjectOneName
    ) {
      allOk = false
      binding.containerSubjectFive.error = "Subject Five Name should be unique and not empty"
    }

    if (allOk) {

      val subjects = listOf(
        subjectOneName,
        subjectTwoName,
        subjectThreeName,
        subjectFourName,
        subjectFiveName
      )
      val subjectFolderPaths = mutableListOf<String>()
      val units = listOf("unit1", "unit2", "unit3", "unit4", "unit5")

      fileViewModel.makeFolder(bookName, "")
      subjects.forEach { subjectName ->

        fileViewModel.makeFolder(subjectName, "/$bookName")
        subjectFolderPaths.add("/$bookName/$subjectName")
      }
      for (i in 0 until 5) {
        for (j in 0 until 5) {

          fileViewModel.makeFolder(units[j], subjectFolderPaths[i])
        }
      }
      val colorString = "#%02x%02x%02x".format((0..200).random(), (0..200).random(), (0..200).random())
      val book = Book(0, bookName, subjects, subjectFolderPaths, units, colorString)
      val subjectGrid = SubjectGrid(
        null,
        bookName,
        subjectFolderPaths[0],
        subjectFolderPaths[1],
        subjectFolderPaths[2],
        subjectFolderPaths[3],
        subjectFolderPaths[4]
      )
      viewModel.insertSubjectGrid(subjectGrid)
      viewModel.insertBook(book)
      findNavController().navigate(R.id.action_subjectGridFragment_to_booksFragment)
    }
  }
}
