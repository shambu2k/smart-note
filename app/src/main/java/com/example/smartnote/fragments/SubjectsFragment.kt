package com.example.smartnote.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnote.R
import com.example.smartnote.adapters.SubjectsAdapter
import com.example.smartnote.databinding.FragmentSubjectsBinding
import com.example.smartnote.db.Book
import com.example.smartnote.helpers.Constants.PERMISSIONS_REQUEST_CODE
import com.example.smartnote.helpers.Constants.SCANNER_REQUEST_CODE
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BookViewModel
import com.example.smartnote.viewmodels.FileViewModel
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class SubjectsFragment : Fragment() {

  private var binding by viewLifecycle<FragmentSubjectsBinding>()

  private var book: Book = Book(0, "book", listOf("1", "2", "3", "4", "5"), listOf("/1", "/2", "/3", "/4", "/5"), listOf("u1", "u2", "u3", "u4", "u5"),"")
  private val viewModel: BookViewModel by lazy {
    ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
  }
  private val fileViewModel: FileViewModel by lazy {
    ViewModelProvider(this).get(FileViewModel::class.java)
  }


  // recycler_view
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
      val bookName: String = args.bookName
      val bundle = bundleOf("bookName" to bookName)
      binding.buttonAddPhoto.setOnClickListener {
        checkPermissionAndScan()
      }
      adapter = SubjectsAdapter(book)
      viewModel.getBookById(bookId).observe(
        viewLifecycleOwner,
        Observer { book ->
          book?.let {
            this.book = book
            adapter.refresh(book)
          }
        }
      )
      recyclerView = binding.recyclerView
      recyclerView.layoutManager = LinearLayoutManager(activity)
      recyclerView.adapter = adapter
    }
  }

  private fun checkPermissionAndScan() {
    if (ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.CAMERA
      ) != PackageManager.PERMISSION_GRANTED ||
      ActivityCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      requestPermissions(
        arrayOf(
          Manifest.permission.CAMERA,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),
        PERMISSIONS_REQUEST_CODE
      )
    } else {
      Log.i("SubjectsFragment", "Permissions Granted")
      val preference = ScanConstants.OPEN_CAMERA
      val intent = Intent(requireContext(), ScanActivity::class.java)
      intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
      startActivityForResult(intent, SCANNER_REQUEST_CODE)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == SCANNER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
      val uri: Uri = data!!.extras!!.getParcelable(ScanConstants.SCANNED_RESULT)!!
      var bitmap: Bitmap? = null
      val contentResolver = requireActivity().contentResolver
      try {
        bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          val source = ImageDecoder.createSource(contentResolver, uri)
          ImageDecoder.decodeBitmap(source)
        } else {
          MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
        contentResolver.delete(uri, null, null)

        val scannedSub = data.extras!!.get("scannedSub")
        val scannedUnit = data.extras!!.get("scannedUnit")

        val path = viewModel.getSubjectFolderPath(bookName = requireArguments().getString("bookName", ""), subNo = scannedSub as Int)

        fileViewModel.storeImage(bitmap, scannedUnit.toString(), path)
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }
}
