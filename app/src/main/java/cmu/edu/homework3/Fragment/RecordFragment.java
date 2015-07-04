package cmu.edu.homework3.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import cmu.edu.homework3.R;
import cmu.edu.homework3.View.MediaRecorderActivity;


/**
 * Created by mizhou on 7/3/15.
 */
public class RecordFragment extends Fragment{
    View view;
    private final String TAG = "----------";
    ImageButton picButton;
    ImageButton videoButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_video, container, false);
        picButton = (ImageButton)view.findViewById(R.id.take_photo);
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mediaRecord = new Intent(getActivity(), MediaRecorderActivity.class );
                startActivity(mediaRecord) ;
            }
        });
        videoButton = (ImageButton)view.findViewById(R.id.record_video);

        return view;
    }
}
