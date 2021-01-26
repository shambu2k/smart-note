package com.example.smartnote.repository

import android.util.Log
import com.example.smartnote.helpers.Constants.TYPE_FOLDER
import com.example.smartnote.helpers.DriveServiceHelper
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.tasks.await

class BackupRepository {

    companion object {
        const val TAG = "BackupRepository"
    }

    suspend fun uploadPDF(
        helper: DriveServiceHelper,
        pdfPath: String
    ) {
        try {
            val details = pdfPath.split("/")
            val pdfName = details[details.size - 1]
            val subjectName = details[details.size - 2]
            val bookName = details[details.size - 3]
            val baseFolderName = "Smart Note"
            val fileList = helper.queryFiles().await()
            val files = fileList!!.files
            var bookFolderID: String? = null
            var subjectFolderID: String? = null
            var baseFolderID: String? = null
            for (file in files) {
                if (file["mimeType"] == TYPE_FOLDER) {
                    when (file["name"]) {
                        bookName -> {
                            bookFolderID = file["id"] as String
                        }
                        baseFolderName -> {
                            baseFolderID = file["id"] as String
                        }
                    }
                }
            }
            if (baseFolderID == null) {
                baseFolderID = helper.createFolder(baseFolderName, "root").await()
            }
            if (bookFolderID == null) {
                bookFolderID = helper.createFolder(bookName, baseFolderID).await()
            }
            subjectFolderID = helper.getFileChildren(bookFolderID, subjectName).await()
            if (subjectFolderID == null) {
                subjectFolderID = helper.createFolder(subjectName, bookFolderID).await()
            }
            val existingFileID = helper.getFileChildren(subjectFolderID, pdfName).await()
            if (existingFileID != null) {
                helper.deleteFile(existingFileID)
            }
            val fileID = helper.createPDF(pdfName, subjectFolderID, pdfPath).await()
            Log.i(TAG, "fileList: $fileList")
            Log.i(TAG, "fileID: $fileID")
        } catch (e: ApiException) {
            Log.w(TAG, "listFilesResult:failed code=" + e.statusCode)
        }
    }
}
