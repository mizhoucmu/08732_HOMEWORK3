package cmu.edu.homework3.View;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cmu.edu.homework3.R;

/**
 * Created by mizhou on 7/3/15.
 */
public class TakePictureNPreview extends Activity implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback {

    Camera mCamera;
    SurfaceView mPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_picture);

        mPreview = (SurfaceView)findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        try {
            mCamera = Camera.open();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    public void onCancelClick(View v) {
        finish();
    }

    public void onSnapClick(View v) {
        mCamera.takePicture(this, null, null, this);
    }

    @Override
    public void onShutter() {
        Toast.makeText(this, "Click!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //Here, we chose internal storage
        try {
            FileOutputStream out = openFileOutput("picture.jpg", Activity.MODE_PRIVATE);
            out.write(data);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width,selected.height);
        mCamera.setParameters(params);

        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.i("PREVIEW", "surfaceDestroyed");
    }
}
