package com.example.smartnote.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.smartnote.MainActivity
import com.example.smartnote.adapters.PagesAdapter
import com.example.smartnote.databinding.FragmentPagesBinding
import com.example.smartnote.db.Pdf
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BookViewModel
import com.example.smartnote.viewmodels.FileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLConnection
import java.util.*

@AndroidEntryPoint
class PagesFragment : Fragment() {

  private var binding by viewLifecycle<FragmentPagesBinding>()
  private val viewModel: FileViewModel by lazy {
    ViewModelProvider(requireActivity()).get(FileViewModel::class.java)
  }
  private val bookViewModel: BookViewModel by lazy {
    ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentPagesBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val args: PagesFragmentArgs by navArgs()
    (activity as MainActivity).supportActionBar?.title = "${args.subjectName} - Unit ${args.unitNo}"
    val fileStrings = mutableListOf<String>()
    val list = context?.let { viewModel.getFiles(args.unitFolderPath, it) }
    if (list != null && list.size > 0) {
      for (currentFile in list) {
        if (currentFile.name.endsWith(".png")) {
          // File absolute path
          Log.i("pdf", currentFile.path)
          // File Name
          Log.i("pdf", currentFile.getName())
          fileStrings.add(currentFile.path)
          Log.i("pdf",fileStrings.size.toString())
        }
      }
    }

    binding.pagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    val adapter = fileStrings.let { PagesAdapter(it) }
    binding.pagesRecyclerView.adapter = adapter

    binding.buttonCreatePdf.setOnClickListener {
      Log.i("pdf","clicked")
      Log.i("pdf",fileStrings.size.toString())
      if(fileStrings.size==0){
        Toast.makeText(activity,"No Images",Toast.LENGTH_SHORT).show()
      }else {

        Log.i("pdf", "not null")
        viewModel.storePdf(
          fileStrings,
          context?.filesDir.toString() + args.unitFolderPath,
          args.unitFolderPath.split('/').toString()
        )
        val pdf = Pdf(
          0,
          args.unitFolderPath.split('/').toString(),
          args.unitFolderPath,
          Calendar.getInstance().time
        )
        //if there is a pdf with same name it will delete it in db first then add newone
        Log.d("filesdir",context?.filesDir.toString())
        Log.d("path",args.unitFolderPath.split('/').toString())
        bookViewModel.deletePdfByName(args.unitFolderPath.split('/').toString())
        bookViewModel.insertPdf(pdf)
        Toast.makeText(activity, "Pdf created successfully", Toast.LENGTH_SHORT).show()
      }
    }
  }
}
