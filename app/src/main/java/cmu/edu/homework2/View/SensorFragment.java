package cmu.edu.homework2.View;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cmu.edu.homework2.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SensorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "-------";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView idLabel;
    private TextView id;
    private TextView timeLabel;
    private TextView sensorLabel;
    private TextView timeTV;
    private TextView sensorTV;
    private View view;
    private SensorManager mSensorManager;
    private List<Sensor> sensorlist;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SensorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SensorFragment newInstance(String param1, String param2) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SensorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);


    }

    private void setTime() {
        timeTV = (TextView)view.findViewById(R.id.current_time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        Log.i(TAG, "Current time is : " + time);
        timeTV.setText(time);
    }

    private void setSensor() {
        sensorTV = (TextView)view.findViewById(R.id.sensor);
        sensorlist = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        StringBuilder sensorstr = new StringBuilder();
        for (Sensor sensor : sensorlist) {
            sensorstr.append(" · ");
            sensorstr.append(sensor.getName());
            sensorstr.append("\n");
        }

        Log.i(TAG, "Available Sensors : " + sensorstr.toString());
        sensorTV.setText(sensorstr.toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        setTime();
        setSensor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sensor, container, false);
        idLabel = (TextView)view.findViewById(R.id.andrewidLabel);
        idLabel.setText("MyAndrewID:");
        id = (TextView)view.findViewById(R.id.andrewId);
        id.setText("mizhou");
        timeLabel = (TextView)view.findViewById(R.id.timeLabel);
        timeLabel.setText("CurrentTime:");
        sensorLabel = (TextView)view.findViewById(R.id.sensorLabel);
        sensorLabel.setText("Available Sensors:");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            setTime();
            setSensor();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
