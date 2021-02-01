package com.example.smartnote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.adapters.BooksAdapter
import com.example.smartnote.databinding.FragmentBooksBinding
import com.example.smartnote.db.Book
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BookViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BooksFragment : Fragment() {
  private var binding by viewLifecycle<FragmentBooksBinding>()
  private var books: List<Book> = listOf()

  // recycler_view
  private lateinit var recyclerView: RecyclerView
  private lateinit var adapter: BooksAdapter

  private val viewModel: BookViewModel by lazy {
    ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
  }

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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpRecyclerView()
  }

  private fun setUpRecyclerView() {
    adapter = context?.let { BooksAdapter(books, it) }!!
    recyclerView = binding.recyclerView
    recyclerView.layoutManager = LinearLayoutManager(activity)
    recyclerView.adapter = adapter
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.books.observe(
      this,
      Observer { books ->
        books?.let {
          this.books = books
          adapter.refresh(this.books)
        }
      }
    )
  }
}
