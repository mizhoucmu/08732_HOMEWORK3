package cmu.edu.homework3.cameraapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import cmu.edu.homework3.MainActivity;
import cmu.edu.homework3.R;
import cmu.edu.homework3.View.SavePicture;

public class MyCamera extends Activity {

    private final String TAG = "----------";
    private Camera mCamera;
    private Context context = this;
    private CameraPreview mPreview;
    public PictureCallback mPicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        Log.d(TAG, "onCreate of MyCamera");

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
                Intent it = new Intent(context, SavePicture.class);
                it.putExtra("data", data);
                Display display = getWindowManager().getDefaultDisplay();
                int rotation = 0;
                switch (display.getRotation()) {
                    case Surface.ROTATION_0: // This is display orientation
                        rotation = 90;
                        break;
                    case Surface.ROTATION_90:
                        rotation = 0;
                        break;
                    case Surface.ROTATION_180:
                        rotation = 270;
                        break;
                    case Surface.ROTATION_270:
                        rotation = 180;
                        break;
                }
                it.putExtra("rotation",rotation);
                startActivity(it);
//                mCamera.startPreview();
//                findViewById(R.id.button_capture).setEnabled(true);
            }
        };

        // Add a listener to the Capture button
        final ImageButton captureButton = (ImageButton) findViewById(R.id.button_capture);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent it = new Intent(context, MainActivity.class);
            startActivity(it);
        }
        return false;

    }

}



