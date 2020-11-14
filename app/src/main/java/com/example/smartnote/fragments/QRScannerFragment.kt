package com.example.smartnote.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartnote.databinding.FragmentQRScannerBinding
import com.example.smartnote.helpers.viewLifecycle
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QRScannerFragment : Fragment() {

    private var binding by viewLifecycle<FragmentQRScannerBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQRScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanQr()

    }

    private fun scanQr(){
        IntentIntegrator.forSupportFragment(this).initiateScan()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                if (Patterns.WEB_URL.matcher(result.contents).matches()) {
                    // Open URL
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(result.contents))
                    startActivity(browserIntent)
                    findNavController().popBackStack()

                }else{
                    Toast.makeText(activity, "Scan a valid QR", Toast.LENGTH_LONG).show()
                    scanQr()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}
