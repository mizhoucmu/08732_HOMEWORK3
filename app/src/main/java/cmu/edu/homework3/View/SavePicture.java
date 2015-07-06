package cmu.edu.homework3.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import cmu.edu.homework3.Controller.MyDevice;
import cmu.edu.homework3.MainActivity;
import cmu.edu.homework3.R;

public class SavePicture extends ActionBarActivity {
    ImageView imgView;
    Button saveButton;
    Button cancelButton;
    Bitmap bitmap;
    Context context;
    public static int MEDIA_TYPE_IMAGE = 1;
    public static int MEDIA_TYPE_VIDEO = 2;

    private static final String TAG = "---------";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_save_picture);
        imgView = (ImageView) findViewById(R.id.pic_to_save);
        showPicture();
        saveButton = (Button) findViewById(R.id.save_pic);
        saveButton.setText("Save");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null) {
                    Log.e("PictureSave", "Error creating media file, check storage permissions: ");
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    printInfo();
                    rescanMedia(pictureFile.getAbsolutePath());
                    Toast.makeText(getApplicationContext(), "Picture taken:" + pictureFile.getAbsolutePath(),
                            Toast.LENGTH_LONG).show();

                } catch (FileNotFoundException e) {
                    Log.e("PictureSave", "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.e("PictureSave", "Error accessing file: " + e.getMessage());
                }
                Intent it = new Intent(context, MainActivity.class);
                startActivity(it);
            }
        });

        cancelButton = (Button) findViewById(R.id.cancel_save_pic);
        cancelButton.setText("Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent it = new Intent(context, MainActivity.class);
                startActivity(it);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void rescanMedia(String filepath) {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        MediaScannerConnection.scanFile(context, new String[]{filepath}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
//                Log.i(TAG, "Scan completed");
//                Log.i(TAG, "path:" + path);
//                Log.i(TAG, "URI" + uri);
            }
        });
    }


    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "homework3");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("--------", "failed to create directory");
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

    private void showPicture() {
        byte[] data = (byte[]) getIntent().getExtras().get("data");
        bitmap = ImageTools.toBitmap(data);
        int rotation = (int) getIntent().getExtras().get("rotation");
        bitmap = ImageTools.rotate(bitmap, rotation);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        imgView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width / 2, height / 2, false));
    }

    private void printInfo() {
        String andrewID = "mizhou";
        String deviceName = MyDevice.getDeviceName();
        String myVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("EST"));
        String timeStamp = df.format(now) + " EST";
        Log.d(TAG, andrewID + ":" + deviceName + " " + myVersion + " : " + timeStamp);
    }

}

class ImageTools {
    public static Bitmap toBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static Bitmap rotate(Bitmap in, int angle) {
        Matrix mat = new Matrix();
        mat.postRotate(angle);
        return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), mat, true);
    }

}