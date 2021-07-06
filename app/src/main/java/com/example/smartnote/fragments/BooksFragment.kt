package com.example.smartnote.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.adapters.BooksAdapter
import com.example.smartnote.adapters.RecentPdfsAdapter
import com.example.smartnote.databinding.FragmentBooksBinding
import com.example.smartnote.db.Book
import com.example.smartnote.db.Pdf
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BookViewModel
import com.example.smartnote.viewmodels.FileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BooksFragment : Fragment() {
  private var binding by viewLifecycle<FragmentBooksBinding>()
  private var books: List<Book> = listOf()
  private var pdfs: List<Pdf> = listOf()
  private var images: List<String> = listOf()

  // recycler_view
  private lateinit var recyclerView: RecyclerView
  private lateinit var adapter: BooksAdapter
  private lateinit var recentView: RecyclerView
  private lateinit var recentPdfsAdapter: RecentPdfsAdapter
  private lateinit var recentsText : TextView
  private lateinit var emptyView : TextView

  private val viewModel: BookViewModel by lazy {
    ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
  }

  private val fileViewModel: FileViewModel by lazy {
    ViewModelProvider(requireActivity()).get(FileViewModel::class.java)
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
    adapter = BooksAdapter(books)
    recyclerView = binding.recyclerView

    recentsText = binding.recentsText
    emptyView = binding.emptyView
    emptyView.visibility = View.VISIBLE

    recentsText.visibility = View.GONE
    recyclerView.visibility = View.GONE

    recyclerView.layoutManager = LinearLayoutManager(activity)
    recyclerView.adapter = adapter

    recentPdfsAdapter = RecentPdfsAdapter(pdfs,images)
    recentView = binding.recentRecyclerView
    recentView.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
    recentView.adapter = recentPdfsAdapter
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.books.observe(
      this,
      Observer { books ->
        books?.let {
          this.books = books
          if(!this.books.isEmpty()){
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
          }
          adapter.refresh(this.books)
        }
      }
    )
    try {
      viewModel.getRecPdfs().observe(
        this,
        Observer { pdfs ->
          this.pdfs = pdfs
          if(!pdfs.isEmpty()){
            recentsText.visibility = View.VISIBLE
          }
          val fileStrings = mutableListOf<String>()
          for (curr in pdfs) {
            val file = activity?.let { fileViewModel.getFirstImage(curr.location, it) }
            fileStrings.add(file!!.path)
          }
          this.images = fileStrings
          Log.i("size", pdfs.size.toString())
          recentPdfsAdapter.refresh(this.pdfs, this.images)
        }
      )
    }catch (e:Exception){
      e.stackTrace
    }








  }
}
