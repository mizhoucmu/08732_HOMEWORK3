package cmu.edu.homework3.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import cmu.edu.homework3.R;

/**
 * Created by mizhou on 7/3/15.
 */
public class RecordFragment extends Fragment {
    View view;
    private final String TAG = "----------";
    ImageButton cameraButton;
    ImageButton videoButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_video, container, false);
        cameraButton = (ImageButton) view.findViewById(R.id.take_photo);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Intent takePic = new Intent(getActivity(), cmu.edu.homework3.CameraAPI.MyCamera.class);
                    startActivity(takePic);
                } else {
                    Log.e(TAG, "camera not availabe");
                }

            }
        });
        videoButton = (ImageButton) view.findViewById(R.id.record_video);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // detect if camera is available
                if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Intent mediaRecord = new Intent(getActivity(), cmu.edu.homework3.Video.VideoCapture3.class);
                    startActivity(mediaRecord);
                } else {
                    Log.e(TAG, "camera not availabe");
                }
            }
        });
        return view;
    }
}
