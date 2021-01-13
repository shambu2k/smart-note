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
        driveServiceHelper: DriveServiceHelper,
        fileName: String, folderName: String
    ) {
        try {
            val fileList = driveServiceHelper.queryFiles().await()
            val files = fileList!!.files
            for (file in files) {
                if (file["mimeType"] == TYPE_FOLDER && file["name"] == folderName) {
                    val fileID =
                        driveServiceHelper.createFile(fileName, file["id"] as String?).await()
                    Log.i(TAG, "fileID: $fileID")
                    return
                }
            }
            val folderID = driveServiceHelper.createFolder(folderName).await()
            val fileID = driveServiceHelper.createFile(fileName, folderID).await()
            Log.i(TAG, "fileList: $fileList")
            Log.i(TAG, "fileID: $fileID")
        } catch (e: ApiException) {
            Log.w(TAG, "listFilesResult:failed code=" + e.statusCode)
        }
    }
}
