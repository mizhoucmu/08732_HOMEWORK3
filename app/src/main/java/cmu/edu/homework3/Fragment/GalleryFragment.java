package cmu.edu.homework3.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
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
    private View view;
    private List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
    private HashMap<String, Image> images = new HashMap<String, Image>();
    private HashMap<String, Video> videos = new HashMap<String, Video>();


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
        listView = (ListView) view.findViewById(R.id.listview);
        simpleAdapter = new SimpleAdapter(getActivity(), getData(), R.layout.gallery_item, new String[]{"pic", "des"}, new int[]{R.id.pic, R.id.describe});
        listView.setOnItemClickListener(this);
        listView.setAdapter(simpleAdapter);
    }

    private List<HashMap<String, Object>> getData() {
        prepareImageInfo();
        prepareImgThumbnailsInfo();
        for (String id : images.keySet()) {
            Image img = images.get(id);
            Log.i(TAG, "id " + img.getImage_id() + "  Thumbnails: " + img.getThumbnail_path());
            HashMap<String, Object> map = new HashMap<String, Object>();
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(getActivity().getContentResolver(), Long.parseLong(id), MediaStore.Images.Thumbnails.MICRO_KIND, null);
//            map.put("pic", bitmap);
            map.put("pic", img.getThumbnail_path());
            String takenTime = usingDateFormatterWithTimeZone(images.get(id).getTime());
            Log.i(TAG, takenTime);
            map.put("des", takenTime);
            map.put("id", id);
            map.put("path", img.getImage_path());
            dataList.add(map);
        }

        prepareVideoThumbnailsInfo();
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
                    videos.get(id).setPath(path);
                    videos.get(id).setTime(time);
                    videos.get(id).setTitle(title);

                } else {
                    Video video = new Video();
                    video.setId(id);
                    video.setPath(path);
                    video.setTitle(title);
                    video.setTime(time);
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


    private void prepareImgThumbnailsInfo() {
        Log.i(TAG, "prepareImgThumbnailsInfo");
        final String[] projection = {MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA,
        };

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                String thumbnail_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));

                if (images.containsKey(id)) {
                    images.get(id).setThumbnail_path(thumbnail_path);
                } else {
                    Image img = new Image();
                    img.setImage_id(id);
                    img.setThumbnail_path(thumbnail_path);
                    images.put(id, img);
                }
                Log.i(TAG, "Image: id " + id + ", thumbnail_path : " + thumbnail_path);

            }
            cursor.close();
        }

    }

    private void prepareVideoThumbnailsInfo() {
        Log.i(TAG, "prepareVideoThumbnailsInfo");
        final String[] projection = {MediaStore.Video.Thumbnails.VIDEO_ID,
                MediaStore.Video.Thumbnails.DATA,
        };

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.VIDEO_ID));
                String thumbnail_path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));

                if (videos.containsKey(id)) {
                    videos.get(id).setThumbnail_path(thumbnail_path);
                } else {
                    Video video = new Video();
                    video.setId(id);
                    video.setThumbnail_path(thumbnail_path);
                    videos.put(id, video);
                }
                Log.i(TAG, "Video: id " + id + ", Video thumbnail_path : " + thumbnail_path);

            }
            cursor.close();
        }

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
                String image_title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));

                if (images.containsKey(id)) {
                    images.get(id).setImage_path(image_path);
                    images.get(id).setTime(date);
                    images.get(id).setTitle(image_title);

                } else {
                    Image img = new Image();
                    img.setImage_id(id);
                    img.setImage_path(image_path);
                    img.setTitle(image_title);
                    img.setTime(date);
                    images.put(id, img);
                }
                Log.i(TAG, "Image: id " + id + ", image path: " + image_path);

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
                it.putExtra("path",path);
                startActivity(it);
                break;
            }
            case "png": {
                Intent it = new Intent(getActivity(), OpenPicture.class);
                it.putExtra("path",path);
                startActivity(it);
                break;
            }
            case "mp4": {
                Intent it = new Intent(getActivity(), cmu.edu.homework3.View.VideoPlayerActivity.class);
                it.putExtra("path",path);
                startActivity(it);
                break;
            }
        }

    }
}
