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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.smartnote.MainActivity
import com.example.smartnote.adapters.PagesAdapter
import com.example.smartnote.databinding.FragmentPagesBinding
import com.example.smartnote.db.Pdf
import com.example.smartnote.helpers.Constants
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BookViewModel
import com.example.smartnote.viewmodels.FileViewModel
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
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
  private val args: PagesFragmentArgs by navArgs()
  private lateinit var adapter: PagesAdapter

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
    binding.buttonAddPhoto.setOnClickListener {
      checkPermissionAndScan()
    }
    (activity as MainActivity).supportActionBar?.title = "${args.subjectName} - Unit ${args.unitNo}"
    val fileStrings = reloadImgs(args.unitFolderPath)

    binding.pagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    val folderPath = args.unitFolderPath
    adapter = PagesAdapter(
      fileStrings,
      activity as MainActivity,
      object : PagesAdapter.DeleteListener {
        override fun deletePages(selectedItems: MutableList<String>) {
          for (item in selectedItems) {
            viewModel.deleteFile(item)
          }
        }
      }
    )
    viewModel.isDeleted.observe(viewLifecycleOwner) {
      if (it) {
        Toast.makeText(requireContext(), "Selected Pages Deleted Successfully!!", Toast.LENGTH_LONG).show()
        viewModel.isDeleted.postValue(false)
        adapter.listImages = reloadImgs(args.unitFolderPath)
        adapter.notifyDataSetChanged()
      }
    }
    viewModel.isStored.observe(viewLifecycleOwner) {
      if (it) {
        viewModel.isStored.postValue(false)
        adapter.listImages = reloadImgs(args.unitFolderPath)
        adapter.notifyDataSetChanged()
      }
    }
    binding.pagesRecyclerView.adapter = adapter

    binding.buttonCreatePdf.setOnClickListener {
      Log.i("pdf", "clicked")
      Log.i("pdf", fileStrings.size.toString())
      if (fileStrings.size == 0) {
        Toast.makeText(activity, "No Images", Toast.LENGTH_SHORT).show()
      } else {

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
        // if there is a pdf with same name it will delete it in db first then add newone
        Log.d("filesdir", context?.filesDir.toString())
        Log.d("path", args.unitFolderPath.split('/').toString())
        bookViewModel.deletePdfByName(args.unitFolderPath.split('/').toString())
        bookViewModel.insertPdf(pdf)
        Toast.makeText(activity, "Pdf created successfully", Toast.LENGTH_SHORT).show()
      }
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
        Constants.PERMISSIONS_REQUEST_CODE
      )
    } else {
      Log.i("SubjectsFragment", "Permissions Granted")
      val preference = ScanConstants.OPEN_CAMERA
      val intent = Intent(requireContext(), ScanActivity::class.java)
      intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
      startActivityForResult(intent, Constants.SCANNER_REQUEST_CODE)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == Constants.SCANNER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
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

        val path = bookViewModel.getSubjectFolderPath(args.bookName, subNo = scannedSub as Int)

        viewModel.storeImage(bitmap, scannedUnit.toString(), path)
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }
  fun reloadImgs(unitPath: String): MutableList<String> {
    val fileStrings = mutableListOf<String>()
    val list = context?.let { viewModel.getFiles(unitPath, it) }
    if (list != null && list.size > 0) {
      for (currentFile in list) {
        if (currentFile.name.endsWith(".jpeg")) {
          // File absolute path
          Log.i("pdf", currentFile.path)
          // File Name
          Log.i("pdf", currentFile.getName())
          fileStrings.add(currentFile.path)
          Log.i("pdf", fileStrings.size.toString())
        }
      }
    }
    return fileStrings
  }
}
