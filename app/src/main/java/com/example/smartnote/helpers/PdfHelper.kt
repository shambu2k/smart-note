package com.example.smartnote.helpers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.smartnote.helpers.Constants.PDF_PAGE_HEIGHT
import com.example.smartnote.helpers.Constants.PDF_PAGE_WIDTH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.roundToInt

class PdfHelper @Inject constructor() {

  private suspend fun createPdfFromImages(
    paths: List<String>,
    outPath: String, // No need to append slash at the end here!
    fileName: String
  ) {

    var pdfDocument = PdfDocument()

    for ((index, path) in paths.withIndex()) {
      val bitmap = getBitmapFromLocalPath(path)
      val pageInfo = PdfDocument.PageInfo.Builder(
        PDF_PAGE_WIDTH,
        PDF_PAGE_HEIGHT,
        index
      ).create()
      val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)
      val canvas: Canvas = page.canvas

      val scaledBitmap = scale(bitmap!!, PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT)
      canvas.drawBitmap(scaledBitmap, (PDF_PAGE_WIDTH - scaledBitmap.width).toFloat() / 2, (PDF_PAGE_HEIGHT - scaledBitmap.height).toFloat() / 2, null)
      pdfDocument.finishPage(page)
    }

    val fileOutputStream = FileOutputStream(outPath + "/$fileName.pdf")
    pdfDocument.writeTo(fileOutputStream)
    fileOutputStream.close()
    pdfDocument.close()
  }

  private fun getBitmapFromLocalPath(path: String): Bitmap? {
    try {
      return BitmapFactory.decodeFile(path)
    } catch (e: Exception) {
    }
    return null
  }

  fun storePdf(paths: List<String>, outPath: String, fileName: String) {
    runBlocking(Dispatchers.IO) {
      createPdfFromImages(paths, outPath, fileName)
    }
  }

  fun getFiles(folderPath: String, context: Context): Array<File>? {
    var list: Array<File>? = null
    val fileSystemHelper = FileSystemHelper(context)
    runBlocking(Dispatchers.IO) {
      try {
        list = fileSystemHelper.getFilesList(folderPath)
      } catch (e: Exception) {
        e.stackTrace
      }
    }
    return list
  }

  fun getFirstImage(folderPath: String, context: Context): File? {
    var file: File? = null
    val fileSystemHelper = FileSystemHelper(context)
    runBlocking(Dispatchers.IO) {
      try {
        file = fileSystemHelper.getFirstImage(folderPath)
      } catch (e: Exception) {
        e.stackTrace
      }
    }
    return file
  }

  private fun scale(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    // Determine the constrained dimension, which determines both dimensions.
    val width: Int
    val height: Int
    val widthRatio = bitmap.width.toFloat() / maxWidth
    val heightRatio = bitmap.height.toFloat() / maxHeight
    // Width constrained.
    if (widthRatio >= heightRatio) {
      width = maxWidth
      height = (width.toFloat() / bitmap.width * bitmap.height).roundToInt()
    } else {
      height = maxHeight
      width = (height.toFloat() / bitmap.height * bitmap.width).roundToInt()
    }
    val scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val ratioX = width.toFloat() / bitmap.width
    val ratioY = height.toFloat() / bitmap.height
    val middleX = width / 2.0f
    val middleY = height / 2.0f
    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
    val canvas = Canvas(scaledBitmap)
    canvas.setMatrix(scaleMatrix)
    canvas.drawBitmap(
      bitmap,
      middleX - bitmap.width / 2,
      middleY - bitmap.height / 2,
      Paint(Paint.FILTER_BITMAP_FLAG)
    )
    bitmap.recycle()
    return scaledBitmap
  }
}
