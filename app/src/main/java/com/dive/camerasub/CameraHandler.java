package com.dive.camerasub;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by vinicius.barbosa on 12/12/2016.
 */


public class CameraHandler {

    private CameraManager _cameraManager;
    private Activity _currentActivity;
    private CaptureRequest.Builder _cameraDefaultRequest;
    private DisplayMetrics _metrics = new DisplayMetrics();


    public CameraHandler(CameraManager cameraManager, Activity currentActivity){
        this._cameraManager = cameraManager;
        this._currentActivity = currentActivity;
    }

    public void startStuff() {
        int permissionCheck = ContextCompat.checkSelfPermission(this._currentActivity, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this._currentActivity, new String[]{Manifest.permission.CAMERA}, 0);
        }

        permissionCheck = ContextCompat.checkSelfPermission(this._currentActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this._currentActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        if ( (ContextCompat.checkSelfPermission(this._currentActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            || (ContextCompat.checkSelfPermission(this._currentActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)){
            return;
        }

        try {
            String[] cameraIds = this._cameraManager.getCameraIdList();
            this._cameraManager.openCamera(cameraIds[0], deviceCallback, null);
        }catch(CameraAccessException ex){

        }
    }

    CameraDevice.StateCallback deviceCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            WindowManager wm = (WindowManager)_currentActivity.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(_metrics);

            ImageReader imageReader = ImageReader.newInstance( _metrics.widthPixels, _metrics.heightPixels, PixelFormat.RGBA_8888, 1 );
            imageReader.setOnImageAvailableListener(imageReaderListener, null);
            Surface defaultSurface = imageReader.getSurface();
            List<Surface> surfaceList = Collections.singletonList(defaultSurface);

            try {
                _cameraDefaultRequest = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                _cameraDefaultRequest.addTarget(defaultSurface);
                camera.createCaptureSession(surfaceList, sessionCallback, null);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        }

        @Override
        public void onDisconnected(CameraDevice camera) {
        }

        @Override
        public void onError(CameraDevice camera, int error) {
        }
    };

    CameraCaptureSession.StateCallback sessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            // Once configured we can start capturing
            try {
                session.capture(_cameraDefaultRequest.build(), captureCallback, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };

    CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };

    /**
     * diffrent formats to save diffrent pictures
     */
    ImageReader.OnImageAvailableListener imageReaderListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {

            Image image = null;
            Bitmap bitmap = null;
            image = imageReader.acquireLatestImage();
            Image.Plane[] planes = image.getPlanes();
            Buffer buffer = planes[0].getBuffer().rewind();
            bitmap = Bitmap.createBitmap( 864, 1421, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);

            FileOutputStream outputStream = null;

            try {

                File externalFile = new File(_currentActivity.getExternalFilesDir(null), createStringDate() + ".jpeg");
                outputStream = new FileOutputStream(externalFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream );

                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
                MediaScannerConnection.scanFile(_currentActivity,
                        new String[] { externalFile.toString() }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    outputStream.close();
                    image.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }




        }
    };

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private String createStringDate(){
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }

    private void getRGBIntFromPlanes(Image.Plane[] planes) {
        ByteBuffer yPlane = planes[0].getBuffer();
        ByteBuffer uPlane = planes[1].getBuffer();
        ByteBuffer vPlane = planes[2].getBuffer();

        int bufferIndex = 0;
        final int total = yPlane.capacity();
        final int uvCapacity = uPlane.capacity();
        final int width = planes[0].getRowStride();

        int[] rgbBuffer = new int[_metrics.heightPixels * width];

        int yPos = 0;
        for (int i = 0; i < _metrics.heightPixels; i++) {
            int uvPos = (i >> 1) * width;

            for (int j = 0; j < width; j++) {
                if (uvPos >= uvCapacity-1)
                    break;
                if (yPos >= total)
                    break;

                final int y1 = yPlane.get(yPos++) & 0xff;

            /*
              The ordering of the u (Cb) and v (Cr) bytes inside the planes is a
              bit strange. The _first_ byte of the u-plane and the _second_ byte
              of the v-plane build the u/v pair and belong to the first two pixels
              (y-bytes), thus usual YUV 420 behavior. What the Android devs did
              here (IMHO): just copy the interleaved NV21 U/V data to two planes
              but keep the offset of the interleaving.
             */
                final int u = (uPlane.get(uvPos) & 0xff) - 128;
                final int v = (vPlane.get(uvPos+1) & 0xff) - 128;
                if ((j & 1) == 1) {
                    uvPos += 2;
                }

                // This is the integer variant to convert YCbCr to RGB, NTSC values.
                // formulae found at
                // https://software.intel.com/en-us/android/articles/trusted-tools-in-the-new-android-world-optimization-techniques-from-intel-sse-intrinsics-to
                // and on StackOverflow etc.
                final int y1192 = 1192 * y1;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                r = (r < 0) ? 0 : ((r > 262143) ? 262143 : r);
                g = (g < 0) ? 0 : ((g > 262143) ? 262143 : g);
                b = (b < 0) ? 0 : ((b > 262143) ? 262143 : b);

                rgbBuffer[bufferIndex++] = ((r << 6) & 0xff0000) |
                        ((g >> 2) & 0xff00) |
                        ((b >> 10) & 0xff);
            }
        }
    }
}
