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
import androidx.navigation.fragment.navArgs
import com.example.smartnote.databinding.FragmentScannerBinding
import com.example.smartnote.helpers.viewLifecycle
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanConstants

import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class ScannerFragment : Fragment() {

    companion object {
        const val SCANNER_REQUEST_CODE = 99
        const val PERMISSIONS_REQUEST_CODE = 9
    }

    private var binding by viewLifecycle<FragmentScannerBinding>()
    var scannedBitmap: Bitmap? = null
    private val args: ScannerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScannerBinding.inflate(inflater, container, false)
        binding.saveButton.setOnClickListener {
            saveImage()
        }
        return binding.root
    }

    private fun getSubjectNumber(): Int =
        when (binding.subjectRadioGroup.checkedRadioButtonId) {
            binding.subjectOne.id -> 1
            binding.subjectTwo.id -> 2
            binding.subjectThree.id -> 3
            binding.subjectFour.id -> 4
            binding.subjectFive.id -> 5
            else -> 0
        }

    private fun getUnitNumber(): Int =
        when (binding.unitRadioGroup.checkedRadioButtonId) {
            binding.unitOne.id -> 1
            binding.unitTwo.id -> 2
            binding.unitThree.id -> 3
            binding.unitFour.id -> 4
            binding.unitFive.id -> 5
            else -> 0
        }

    fun saveImage() {
        if (scannedBitmap == null) {
            Toast.makeText(requireContext(), "Scan Image First", Toast.LENGTH_SHORT).show()
            return
        }
        val subjectNumber = getSubjectNumber()
        val unitNumber = getUnitNumber()
        if (subjectNumber == 0 || unitNumber == 0) {
            Toast.makeText(requireContext(), "Choose subject and unit number", Toast.LENGTH_SHORT).show()
            return
        }
        val bookName = args.bookName
        //call storeImageFunction after getting path
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
        } else {
            Log.i("ScannerFragment", "Permissions Granted")
        }

        binding.scanButton.setOnClickListener {
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
            val contentResolver = requireActivity().contentResolver
            try {
                scannedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }
                contentResolver.delete(uri, null, null)
                binding.scannedImage.setImageBitmap(scannedBitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
