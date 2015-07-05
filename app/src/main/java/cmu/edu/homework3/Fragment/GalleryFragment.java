package cmu.edu.homework3.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import cmu.edu.homework3.DataType.Image;
import cmu.edu.homework3.DataType.Video;
import cmu.edu.homework3.R;
import cmu.edu.homework3.View.OpenPicture;

;

/**
 * Created by mizhou on 6/30/15.
 */

public class GalleryFragment extends Fragment implements AdapterView.OnItemClickListener {

    private final String TAG = "----------";
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private View view = null;
    private List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
    private HashMap<String, Image> images = new HashMap<String, Image>();
    private HashMap<String, Video> videos = new HashMap<String, Video>();
    public static int MEDIA_TYPE_IMAGE = 1;
    public static int MEDIA_TYPE_VIDEO = 2;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallery_fragment, container, false);
        setupListView();
        return view;
    }


    private void setupListView() {
        rescanMedia();
        dataList.clear();
        listView = (ListView) view.findViewById(R.id.listview);
        simpleAdapter = new SimpleAdapter(getActivity(), getData(), R.layout.gallery_item, new String[]{"pic", "des"}, new int[]{R.id.pic, R.id.describe});
        listView.setOnItemClickListener(this);
        listView.setAdapter(simpleAdapter);
    }

    private List<HashMap<String, Object>> getData() {
        prepareImageInfo();
        for (String id : images.keySet()) {
            Image img = images.get(id);
            Log.i(TAG, "id " + img.getImage_id() + "  Thumbnails: " + img.getThumbnail_path());
            HashMap<String, Object> map = new HashMap<String, Object>();
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(getActivity().getContentResolver(), Long.parseLong(id), MediaStore.Images.Thumbnails.MICRO_KIND, null);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int new_width = width * 100 / Math.max(width, height);
            int new_height = height * 100 / Math.max(width, height);
            map.put("pic", img.getThumbnail_path());
            String takenTime = usingDateFormatterWithTimeZone(images.get(id).getTime());
            Log.i(TAG, takenTime);
            map.put("des", takenTime);
            map.put("id", id);
            map.put("path", img.getImage_path());
            dataList.add(map);
        }

        prepareVideoInfo();
        for (String id : videos.keySet()) {
            Video video = videos.get(id);

            if (video.getTime() != null) {
                Log.i(TAG, "id " + video.getId() + ": " + video.getPath());
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("pic", video.getThumbnail_path());
                String takenTime = usingDateFormatterWithTimeZone(video.getTime());
                Log.i(TAG, takenTime);
                map.put("des", takenTime);
                map.put("id", id);
                map.put("path", video.getPath());
                dataList.add(map);
            }


        }

        return dataList;
    }

    private void prepareVideoInfo() {
        final String[] projection = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATE_TAKEN};

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                MediaStore.Video.Media.DATE_TAKEN + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String time = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));

                if (videos.containsKey(id)) {
                    Video video = videos.get(id);
                    video.setPath(path);
                    video.setTime(time);
                    video.setTitle(title);
                    video.setThumbnail_path(saveThumbnail(video.getPath()));
                } else {
                    Video video = new Video();
                    video.setId(id);
                    video.setPath(path);
                    video.setTitle(title);
                    video.setTime(time);
                    video.setThumbnail_path(saveThumbnail(video.getPath()));
                    videos.put(id, video);
                }
                Log.i(TAG, "Video: id " + id + ", date taken: " + time);

            }
            cursor.close();
        }
    }

    private String usingDateFormatterWithTimeZone(String time) {
        if (time == null) {
            return null;
        }
        Log.i(TAG, time);
        long input = Long.valueOf(time);
        Date date = new Date(input);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("EST"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);
    }


    private void prepareImageInfo() {
        final String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATE_TAKEN};

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                MediaStore.Images.Media.DATE_TAKEN + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                if (getFileType(image_path).equals("jpg")) {
                    Log.d(TAG,"Going to process JPG: " + image_path);
                    String image_title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                    String date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
                    if (images.containsKey(id)) {
                        images.get(id).setImage_path(image_path);
                        images.get(id).setTime(date);
                        images.get(id).setTitle(image_title);
                        images.get(id).setThumbnail_path(saveThumbnail(image_path));

                    } else {
                        Image img = new Image();
                        img.setImage_id(id);
                        img.setImage_path(image_path);
                        img.setTitle(image_title);
                        img.setTime(date);
                        img.setThumbnail_path(saveThumbnail(image_path));
                        images.put(id, img);
                    }
                    Log.i(TAG, "Image: id " + id + ", image path: " + image_path);
                }
            }
            cursor.close();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick");
        Log.d(TAG, "Clicking: " + dataList.get(position).get("path"));
        String path = (String) dataList.get(position).get("path");
        int dotpos = path.indexOf('.');
        String suffix = path.substring(dotpos + 1);
        Log.d(TAG, "suffix: " + suffix);

        switch (suffix) {
            case "jpg": {
                Intent it = new Intent(getActivity(), OpenPicture.class);
                it.putExtra("path", path);
                startActivity(it);
                break;
            }
            case "png": {
                Intent it = new Intent(getActivity(), OpenPicture.class);
                it.putExtra("path", path);
                startActivity(it);
                break;
            }
            case "mp4": {
                Intent it = new Intent(getActivity(), cmu.edu.homework3.View.VideoPlayerActivity.class);
                it.putExtra("path", path);
                startActivity(it);
                break;
            }
        }

    }

    private String getStoragePath() {
        final String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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

    private void rescanMedia() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.d(TAG, "Path of rescan: " + path.getAbsolutePath());
        MediaScannerConnection.scanFile(getActivity(), new String[]{path.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i(TAG, "Scan completed");
                Log.i(TAG, "path:" + path);
                Log.i(TAG, "URI" + uri);
            }
        });
    }

    public void setMenuVisibility(final boolean visible) {
        if (view != null) {
            super.setMenuVisibility(visible);
            setupListView();
        }
    }


    private String getFileType(String file_path) {
        if (file_path == null) {
            return null;
        }
        int dotpos = file_path.indexOf(".");
        String type = file_path.substring(dotpos + 1);
        return type;
    }

    private String saveThumbnail(String file_path) {
        int dotpos = file_path.indexOf(".");
        String type = file_path.substring(dotpos + 1);
        Log.d(TAG, "Type:" + type);
        String thumbnail_path = getThumbnailPath(file_path);
        Log.d(TAG, "thumbnail_path: " + thumbnail_path);
        switch (type) {
            case "jpg": {
                File imgFile = new File(file_path);
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                File thumbnail = new File(thumbnail_path);
                if (thumbnail == null) {
                    Log.d(TAG,"thumbnail == null");
                }
                if (!thumbnail.exists()) {
                    try {
                        Log.d(TAG, "Creating new file: " + thumbnail_path);
                        FileOutputStream fos = new FileOutputStream(thumbnail);
                        int original_width = bitmap.getWidth();
                        int original_height = bitmap.getHeight();
                        int max_side = Math.max(original_height, original_width);
                        int new_width = original_width * 300 / max_side;
                        int new_height = original_height * 300 / max_side;
                        Bitmap.createScaledBitmap(bitmap, new_width, new_height, false).compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                        Log.d("Thumbnail saved to ", "Path:" + thumbnail_path);
                    } catch (FileNotFoundException e) {
                        Log.d("PictureSave", "File not found: " + e.getMessage());
                    } catch (IOException e) {
                        Log.d("PictureSave", "Error accessing file: " + e.getMessage());
                    }
                }

                break;
            }
            case "mp4": {
                Log.d(TAG, "Going to generate thumbnail for mp4 file" + file_path);
                File videoFile = new File(file_path);
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file_path, MediaStore.Video.Thumbnails.MINI_KIND);
                Log.d(TAG, "bitmap of video: w " + bitmap.getWidth() + ", h " + bitmap.getHeight());
                File thumbnail = new File(thumbnail_path);
                if (!thumbnail.exists()) {
                    Log.d(TAG, "thumbnail doesn't exist");
                    try {
                        Log.d(TAG, "Creating new file: " + thumbnail_path);
                        FileOutputStream fos = new FileOutputStream(thumbnail_path);
                        int original_width = bitmap.getWidth();
                        int original_height = bitmap.getHeight();
                        int max_side = Math.max(original_height, original_width);
                        Log.d(TAG, "max_side:" + max_side);
                        if (max_side > 300) {
                            Log.d(TAG, "max_side > 300");
                            int new_width = original_width * 300 / max_side;
                            int new_height = original_height * 300 / max_side;
                            Bitmap thumbnail_bitmap = Bitmap.createScaledBitmap(bitmap, new_width, new_height, false);
                            thumbnail_bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                            Log.d(TAG, "Thumbnail saved to " + "Path:" + thumbnail_path);
                        } else {
                            Log.d(TAG, "max_side <= 300");
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                            Log.d(TAG, "Thumbnail saved to " + "Path:" + thumbnail_path);
                        }

                    } catch (FileNotFoundException e) {
                        Log.d("PictureSave", "File not found: " + e.getMessage());
                    } catch (IOException e) {
                        Log.d("PictureSave", "Error accessing file: " + e.getMessage());
                    }
                }
                break;
            }
        }
        return thumbnail_path;
    }

    private static String getThumbnailPath(String path) {
        Log.d("----------", "getThumbnailPath from " + path);



        String[] dirs = path.split("/");
        StringBuilder res = new StringBuilder();
        for (int i = 1; i < dirs.length - 2; i++) {
            res.append(File.separator);
            res.append(dirs[i]);
        }
        res.append(File.separator);
        res.append("thumbnail");

        File thumbDir = new File(res.toString());
        if (!thumbDir.exists()) {
            if (!thumbDir.mkdirs()) {
                Log.d("--------", "failed to create directory");
                return null;
            }
        }

        res.append(File.separator);
        res.append(dirs[dirs.length - 1]);

        String result = res.toString();
        int dotpos = result.indexOf(".");
        String finalresult = result.substring(0, dotpos) + ".png";
        Log.d("----------", "getThumbnailPath to " + finalresult);
        return finalresult;
    }
}
