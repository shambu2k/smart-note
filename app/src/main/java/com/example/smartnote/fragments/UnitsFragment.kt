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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartnote.MainActivity
import com.example.smartnote.adapters.UnitsAdapter
import com.example.smartnote.databinding.FragmentUnitsBinding
import com.example.smartnote.helpers.Constants
import com.example.smartnote.helpers.viewLifecycle
import com.example.smartnote.viewmodels.BookViewModel
import com.example.smartnote.viewmodels.FileViewModel
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class UnitsFragment : Fragment() {

  private val bookViewModel: BookViewModel by lazy {
    ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
  }
  private val fileViewModel: FileViewModel by lazy {
    ViewModelProvider(this).get(FileViewModel::class.java)
  }
  private val args: UnitsFragmentArgs by navArgs()

  private var binding by viewLifecycle<FragmentUnitsBinding>()
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    binding = FragmentUnitsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (activity as MainActivity).supportActionBar?.title = args.subjectName
    val path = args.subjectFolderPath
    val adapter = UnitsAdapter(args.bookName, path, args.subjectName)
    binding.unitsRecyclerView.layoutManager = LinearLayoutManager(activity)
    binding.unitsRecyclerView.adapter = adapter
    binding.buttonAddPhoto.setOnClickListener {
      checkPermissionAndScan()
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

        fileViewModel.storeImage(bitmap, scannedUnit.toString(), path)
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }
}
