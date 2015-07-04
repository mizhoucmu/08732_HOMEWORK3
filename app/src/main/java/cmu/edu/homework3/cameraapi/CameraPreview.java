package cmu.edu.homework3.cameraapi;

import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Activity activity;

    public CameraPreview(Activity activity, Context context, Camera camera) {
        super(context);
        mCamera = camera;this.activity = activity;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            
            correctOrientation();
            
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e("CameraPreview-surfaceCreated", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    	Log.d("CameraPreview-surfaceDestroyed", "surfaceDestroyed");
    	mCamera.release();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
    	
        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            
            correctOrientation();
            
            mCamera.startPreview();
            
            Log.e("tag", "Preview Started");

        } catch (Exception e){
            Log.d("Tag", "Error starting camera preview: " + e.getMessage());
        }
    } 
    
    /**
     * Returns the device rotation
     * 
     * @return ROTATION_0 = 0, ROTATION_90 = 90, ROTATION_180 = 180, ROTATION_270 = 270
     */
    private int getRotationOfActivity() {
    	
    	return activity.getWindowManager().getDefaultDisplay().getRotation();
    }
    
    /**
     * Returns the device orientation
     * 
     * @return 1 (Portrait), 2 (Landscape)
     */
    private int getOrientation() {
    	
    	return getResources().getConfiguration().orientation;
    }   
    
    /**
     * Correct the orientation of the Preview based on the Device Orientation and Rotation
     */
    private void correctOrientation() {
    	if(getOrientation() == 1) {
        	mCamera.setDisplayOrientation(90);
        } else if(getOrientation() == 2 && getRotationOfActivity() == 3) {
        	mCamera.setDisplayOrientation(180);
        }
    }
}