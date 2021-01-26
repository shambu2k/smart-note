package com.example.smartnote.helpers

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.util.Log
import com.squareup.picasso.Picasso
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileOutputStream
import java.lang.Exception
import javax.inject.Inject


class PdfHelper @Inject constructor() {

    suspend fun createPdfFromImages(
      paths: List<String>,
      outPath: String, // No need to append slash at the end here!
      fileName: String
    ) {

      var pdfDocument = PdfDocument()

      for ((index, path) in paths.withIndex()) {
        val bitmap = getBitmapFromLocalPath(path)
        val pageInfo = PdfDocument.PageInfo.Builder(
          bitmap!!.width, bitmap!!.height,
          index
        ).create()
        val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        pdfDocument.finishPage(page)
      }

      val fileOutputStream = FileOutputStream( outPath + "/${fileName}.pdf")
      pdfDocument.writeTo(fileOutputStream)
      fileOutputStream.close()
      pdfDocument.close()
      Log.i("pdf","reached here")
    }

    private fun getBitmapFromLocalPath(path: String): Bitmap? {
      try {
        return BitmapFactory.decodeFile(path)
      } catch (e: Exception) {
        Log.d("PdfHelper", e.toString())
      }
      return null
    }
}

