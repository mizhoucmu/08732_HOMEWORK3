package cmu.edu.homework3.Video;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import cmu.edu.homework3.Controller.MyDevice;
import cmu.edu.homework3.Controller.VideoControllerView;
import cmu.edu.homework3.MainActivity;
import cmu.edu.homework3.R;

public class SaveVideo extends ActionBarActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl {

    private Button saveButton;
    private Button cancelButton;
    private String video_path;
    private final String TAG = "---------";

    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerView controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_video);
        setVideoSize();
        video_path = (String) getIntent().getExtras().get("path");

        saveButton = (Button) findViewById(R.id.save_video);
        saveButton.setText("Save");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SaveVideo.this, MainActivity.class);
                startActivity(it);
                rescanMedia(video_path);
                printInfo();
            }
        });

        cancelButton = (Button) findViewById(R.id.cancel_save_video);
        cancelButton.setText("Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete this file
                File videoFile = new File(video_path);
                if (videoFile.exists()) {
                    if (videoFile.delete()) {
//                        Log.d(TAG, "File deleted : " + video_path);
                    } else {
                        Log.e(TAG, "Can't delete File : " + video_path);
                    }
                }
                Intent it = new Intent(SaveVideo.this, MainActivity.class);
                startActivity(it);
            }
        });

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);
        player = new MediaPlayer();
        controller = new VideoControllerView(this);
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, Uri.parse(video_path));
            player.setOnPreparedListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save_video, menu);
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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setDisplay(holder);
            player.prepareAsync();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        player.start();
    }

    // End MediaPlayer.OnPreparedListener
    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }

    private void rescanMedia(String filepath) {
        MediaScannerConnection.scanFile(SaveVideo.this, new String[]{filepath}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
//                Log.i(TAG, "Scan completed");
//                Log.i(TAG, "path:" + path);
//                Log.i(TAG, "URI" + uri);
            }
        });
    }


    private void setVideoSize() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int heigth = display.getHeight();
        SurfaceView videoSurfaceSurfaceView = (SurfaceView) findViewById(R.id.videoSurface);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoSurfaceSurfaceView.getLayoutParams();

        params.height = heigth / 2;
        params.width = width / 2;
        videoSurfaceSurfaceView.setLayoutParams(params);
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
