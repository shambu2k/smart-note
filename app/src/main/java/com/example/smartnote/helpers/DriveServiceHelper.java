package com.example.smartnote.helpers;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.example.smartnote.helpers.Constants.TYPE_FOLDER;
import static com.example.smartnote.helpers.Constants.TYPE_PDF;


public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;
    private static final String TAG = "DriveServiceHelper";

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    public Task<FileList> queryFiles() {
        return Tasks.call(mExecutor, () ->
                mDriveService.files().list().setSpaces("drive").execute());
    }

    public Task<String> createPDF(String pdfName, String folderID, String pdfPath) {
        if (folderID == null) {
            folderID = "root";
        }
        java.io.File pdfFile = new java.io.File(pdfPath);
        FileContent fileContent = new FileContent(TYPE_PDF, pdfFile);
        String finalFolderID = folderID;
        return Tasks.call(mExecutor, () -> {
            File metadata = new File()
                    .setParents(Collections.singletonList(finalFolderID))
                    .setMimeType(TYPE_PDF)
                    .setName(pdfName);

            File googleFile = mDriveService.files().create(metadata, fileContent).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }

            return googleFile.getId();
        });
    }

    public Task<Void> deleteFile(String fileId) {
        return Tasks.call(mExecutor, () -> {
            try {
                mDriveService.files().delete(fileId).execute();
            } catch (IOException e) {
                Log.i(TAG, "An error occurred: " + e);
            }
            return null;
        });
    }


    public Task<String> createFolder(String folderName, String parentFolderID) {
        return Tasks.call(mExecutor, () -> {
            File metadata = new File()
                    .setParents(Collections.singletonList(parentFolderID))
                    .setMimeType(TYPE_FOLDER)
                    .setName(folderName);

            File googleFile = mDriveService.files().create(metadata).execute();
            if (googleFile == null) {
                throw new IOException("Null result when requesting file creation.");
            }

            return googleFile.getId();
        });
    }

    public Task<String> getFileChildren(String fileID, String fileName) {
        return Tasks.call(mExecutor, () -> {
            try {
                List<File> files = mDriveService.files().list().setQ("'" + fileID + "'" + " in parents and name = '" + fileName + "'").execute().getFiles();
                if (files.size() != 0) {
                    Log.i(TAG, "found");
                    return (String) files.get(0).get("id");
                } else {
                    Log.i(TAG, "not found");
                }
            } catch (IOException e) {
                Log.i(TAG, "An error occurred: " + e);
            }
            return null;
        });
    }

}
