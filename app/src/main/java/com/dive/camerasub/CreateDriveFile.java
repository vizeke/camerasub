/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dive.camerasub;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.*;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.*;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An activity to illustrate how to create a file.
 */
public class CreateDriveFile {

    /**
     * Request code for auto Google Play Services error resolution.
     */

    private static final String TAG = "CreateDriveFile";

    private byte[] mBytes;
    private File mFile;
    private CameraActivity mActivity;
    private DriveId mFolderDriveId;

    private GoogleApiClient getGoogleApiClient() {
        return mActivity.getGoogleApiClient();
    }

    public CreateDriveFile(CameraActivity activity) {
        mActivity = activity;
    }

    public void SavePicture(File file, byte[] bytes) {
        mFile = file;
        mBytes = bytes;

        // Drive.DriveApi.requestSync(getGoogleApiClient()).await();

        DriveFolder folder = Drive.DriveApi.getRootFolder(getGoogleApiClient());
        folder.listChildren(getGoogleApiClient()).setResultCallback(childrenRetrievedCallback);
    }

    private void saveFile(){
        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(driveContentsCallback);
    }

    private void createFolder() {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(Configuration.mDefaultFolder).build();

        Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                getGoogleApiClient(), changeSet).setResultCallback(folderCreatedCallback);
    }

    final private ResultCallback<MetadataBufferResult> childrenRetrievedCallback = new
            ResultCallback<MetadataBufferResult>() {
                @Override
                public void onResult(MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        // showMessage("Problem while retrieving files");
                        return;
                    }

                    boolean folderExists = false;

                    for( Metadata action : result.getMetadataBuffer() ){
                        if (action.getTitle().equals(Configuration.mDefaultFolder)){
                            folderExists = true;
                            mFolderDriveId = action.getDriveId();
                            saveFile();
                        }
                    }
                    if (!folderExists){
                        createFolder();
                    }
                    // mResultsAdapter.clear();
                    // mResultsAdapter.append(result.getMetadataBuffer());
                }
            };

    final private ResultCallback<DriveFolderResult> folderCreatedCallback = new
            ResultCallback<DriveFolderResult>() {
                @Override
                public void onResult(DriveFolderResult result) {
                    if (!result.getStatus().isSuccess()) {
                        // showMessage("Error while trying to create the folder");
                        return;
                    }
                    // showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
                    mFolderDriveId = result.getDriveFolder().getDriveId();
                    saveFile();
                }
            };

    final private ResultCallback<DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        // showMessage("Error while trying to create new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            try {
                                outputStream.write(mBytes);
                                outputStream.close();
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }

                            DriveFolder folder = mFolderDriveId.asDriveFolder();

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(mFile.getAbsoluteFile().getName())
                                    .setMimeType("image/jpeg")
                                    .build();

                            // create a file on root folder
                            folder.createFile(getGoogleApiClient(), changeSet, driveContents)
                                  .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFileResult> fileCallback = new
            ResultCallback<DriveFileResult>() {
                @Override
                public void onResult(DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        // showMessage("Error while trying to create the file");
                        return;
                    }
                    // showMessage("Created a file with content: " + result.getDriveFile().getDriveId());
                }
            };

}
