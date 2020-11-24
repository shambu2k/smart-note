package com.example.smartnote.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.adapters.SubjectsAdapter
import com.example.smartnote.databinding.FragmentSubjectsBinding
import com.example.smartnote.db.Book
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BookViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectsFragment : Fragment(){

    private var binding by viewLifecycle<FragmentSubjectsBinding>()

    private var book: Book = Book(0,"book", listOf("1","2","3","4","5"), listOf("/1","/2","/3","/4","/5") , listOf("u1","u2","u3","u4","u5"), listOf("/u1","/u2","/u3","/u4","/u5"))
    private val viewModel: BookViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
    }

    //recycler_view
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SubjectsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val args: SubjectsFragmentArgs by navArgs()
            val bookId: Int = args.bookId
            adapter = SubjectsAdapter(book)
            viewModel.getBookById(bookId).observe(viewLifecycleOwner, Observer { book ->
                book?.let {
                    this.book = book
                    adapter.refresh(book) }
            })
            recyclerView = binding.recyclerView
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.adapter = adapter
        }
    }
}
