package cmu.edu.homework3.View;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

import cmu.edu.homework3.R;

public class OpenPicture extends Activity {
    private final String TAG = "--------";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_picture);
        ImageView imgview = (ImageView) findViewById(R.id.OpenImage);

        String img_path = (String) getIntent().getExtras().get("path");


        File imgFile = new File(img_path);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgview.setImageBitmap(myBitmap);
        }

    }


}
