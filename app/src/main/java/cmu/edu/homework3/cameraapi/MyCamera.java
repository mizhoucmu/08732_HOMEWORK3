package cmu.edu.homework3.cameraapi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cmu.edu.homework3.R;

public class MyCamera extends Activity {

    private final String TAG = "----------";
    private Camera mCamera;
    private Context context = this;
    private CameraPreview mPreview;
    public PictureCallback mPicture;

    public static int MEDIA_TYPE_IMAGE = 1;
    public static int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.d(TAG, "onCreate of MyCamera");

        if (!checkCameraHardware(getApplicationContext())) {
            Log.e("Camera", "Not supported");
        } else {
            Log.d("Camera", "Supported");
        }

        // Create an instance of Camera
        mCamera = getCameraInstance();

        if (mCamera == null) {
            Log.e("GetCameraInstance", "Failed to get a Camera instance.");
            Toast.makeText(getApplicationContext(), "Error opening the camera.",
                    Toast.LENGTH_LONG).show();
        } else {
            // Do nothing
        }

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, this, mCamera);

        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);


        mPicture = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                Log.d("onPictureTaken", "Pic Taken");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hhmmss");
                String date = dateFormat.format(new Date());
                String time = timeFormat.format(new Date());
                String photoFile = "IMG_" + date + "_" + time + "_HDR.jpg";
                String path = getImgStoragePath();
                String filename = path + File.separator + photoFile;
                Log.d(TAG, "filename at :" + filename);
//                MediaScannerConnection.scanFile(context, new String[]{filename}, null, new MediaScannerConnection.OnScanCompletedListener() {
//                    public void onScanCompleted(String path, Uri uri) {
//                        Log.i(TAG, "Scan completed");
//                    }
//                });

                if (path == null) {
                    Log.e("PictureSave", "Error creating media file, check storage permissions: ");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(filename);
                    fos.write(data);
                    fos.close();
                    Toast.makeText(getApplicationContext(), "Picture taken:" + filename,
                            Toast.LENGTH_LONG).show();
                    Log.d("PictureSave", "Path:" + filename);

                    mCamera.startPreview();

                    findViewById(R.id.button_capture).setEnabled(true);

                } catch (FileNotFoundException e) {
                    Log.d("PictureSave", "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d("PictureSave", "Error accessing file: " + e.getMessage());
                }
            }
        };

        // Add a listener to the Capture button
        final Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);

                        Log.d("Capture", "Picture taken.");

                        captureButton.setEnabled(false);
                    }
                }
        );

        final Button zoomInButton = (Button) findViewById(R.id.button_zoom_in);
        zoomInButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Parameters p = mCamera.getParameters();

                        if (p.isZoomSupported()) {

                            int maxZoom = p.getMaxZoom();
                            int currentZoom = p.getZoom();

                            Log.e("ZoomIn", "Current:" + currentZoom + ", Max:" + maxZoom);

                            if (currentZoom < maxZoom) {
                                p.setZoom(currentZoom + 1);
                                mCamera.setParameters(p);
                            }
                        }
                    }
                }
        );


        final Button zoomOutButton = (Button) findViewById(R.id.button_zoom_out);
        zoomOutButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Parameters p = mCamera.getParameters();

                        if (p.isZoomSupported()) {

                            int maxZoom = p.getMaxZoom();
                            int currentZoom = p.getZoom();

                            Log.e("ZoomOut", "Current:" + currentZoom + ", Max:" + maxZoom);

                            if (currentZoom > 0) {
                                p.setZoom(currentZoom - 1);
                                mCamera.setParameters(p);
                            }
                        }
                    }
                }
        );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     *
     * @return The camera instance (if successful)
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private String getImgStoragePath() {
        final String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA};

        Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                null);
        StringBuilder result = new StringBuilder();
        if (cursor != null) {
            while (result.length() == 0 && cursor.moveToNext()) {
                String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                image_path = image_path.trim();
                int dotpos = image_path.indexOf(".");
                String[] dirs = image_path.split(File.separator);
                for (int i = 1; i < dirs.length - 1; i++) {
                    result.append(File.separator);
                    result.append(dirs[i]);
                }
            }
            cursor.close();
        }
        if (result.length() == 0) {
            return null;
        } else {
            return result.toString();
        }
    }


}
