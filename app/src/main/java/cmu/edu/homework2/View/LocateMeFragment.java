package cmu.edu.homework2.View;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocateMeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocateMeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocateMeFragment extends SupportMapFragment implements GoogleMap.OnMyLocationChangeListener,OnMyLocationButtonClickListener,OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private GoogleMap googleMap;
    private Marker marker;
    private String markerString = "";
    LocationManager locationManager;
    private GoogleApiClient mLocationClient = null;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(1000)         // 1 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    private Double latitude;
    private Double longitude;

    private static final String TAG = "-------";

    private boolean mVisible = false;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocateMeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocateMeFragment newInstance(String param1, String param2) {
        LocateMeFragment fragment = new LocateMeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LocateMeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG,"onCreateView");
        return view;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG,"buildGoogleApiClient");
        mLocationClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        if (mVisible) {
            setUpMapIfNeeded();
            buildGoogleApiClient();
            mLocationClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        stopUpdatingMap();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG,"onAttach");
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
        Log.i(TAG,"onDetach");
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onLocationChanged(Location location) {
//        Log.i(TAG,"onLocationChanged");
        if (googleMap != null && mLocationClient != null && mLocationClient.isConnected()) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            if (marker == null) {
                Double lat = location.getLatitude();
                Double lng = location.getLongitude();
                Log.i(TAG,lat.toString() +"," + lng.toString());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat,lng)).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)));
                doReverseGeocoding(location);
            }
//            Log.i(TAG, "[" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "]:" + markerString);

        }


    }

    private void doReverseGeocoding(Location location) {
        // Since the geocoding API is synchronous and may take a while.  You don't want to lock
        // up the UI thread.  Invoking reverse geocoding in an AsyncTask.
        (new ReverseGeocodingTask(getActivity())).execute(new Location[]{location});
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i(TAG,"onMarkerClick");
        Log.i(TAG, markerString);
        marker.setTitle(markerString);
        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient,
                REQUEST, this);  // LocationListener
        Log.i(TAG,"onConnected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG,"onConnectionFailed:" + connectionResult.toString());

    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.i(TAG,"onMyLocationChange");
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        if (marker == null) {
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat,lng)).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)));
//            googleMap.setOnMyLocationChangeListener(this);
        }
        doReverseGeocoding(location);
        Log.i(TAG, "[" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "]:" + markerString);

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

    private void setUpMapIfNeeded() {
        Log.i(TAG,"setUpMapIfNeeded");
        if (googleMap == null) {
            googleMap = super.getMap();
            if (googleMap != null) {
                Log.d(TAG, "Successfully get google map");
                setUpMap();
            }
            // check if map is created successfully or not
            if (googleMap == null) {
                Log.i(TAG,"Sorry! Unable to create maps");
            }
        }
    }

    private void setUpMap() {
        Log.i(TAG, "setUpMap");
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnMarkerClickListener(this);
    }



// Since the geocoding API is synchronous and may take a while.  You don't want to lock
// up the UI thread.  Invoking reverse geocoding in an AsyncTask.
// AsyncTask encapsulating the reverse-geocoding API.

private class ReverseGeocodingTask extends AsyncTask<Location, Void, String> {
    Context mContext;

    public ReverseGeocodingTask(Context context) {
        super();
        mContext = context;
    }

    @Override
    protected String doInBackground(Location... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        Location loc = params[0];
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AddEventActivity",
                    "IO Exception in getFromLocation()");
            // Update address field with the exception.
            // Message.obtain(mHandler, UPDATE_ADDRESS, e.toString()).sendToTarget();
            Log.d("IOException", loc.getLatitude() + ", " + loc.getLongitude());
            return ("IO Exception trying to get address");

        } catch (IllegalArgumentException e2) {
            // Error message to post in the log
            String errorString = "Illegal arguments " +
                    Double.toString(loc.getLatitude()) +
                    " , " +
                    Double.toString(loc.getLongitude()) +
                    " passed to address service";
            Log.e("AddEventActivity", errorString);
            e2.printStackTrace();
            return errorString;
        } catch (Exception e3) {
            // Error message to post in the log
            String errorString = "Unkown Reason" +

                    " GPS code couldn't pass to address service";
            Log.e("AddEventActivity", errorString);
            e3.printStackTrace();
            return errorString;
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            // Format the first line of address (if available), city, and country name.
            String curmarkerString = String.format("%s, %s, %s",
                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "/n",
                    address.getLocality(),
                    address.getPostalCode());
            // Update address field on UI.
//            Log.d("Address Found", markerString);
            if (markerString.isEmpty()) {
                markerString = curmarkerString;
            }
            return markerString;
        } else {
            Log.e("AddEventActivity", "Address Not Found");
            return "No address found";
        }
    }

    @Override
    protected void onPostExecute(String address) {
        // Set activity indicator visibility to "gone"
    }
}



    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            Log.i(TAG, "Going to become visible");
            mVisible = true;
            if (mLocationClient == null) {
                setUpMapIfNeeded();
                buildGoogleApiClient();
            }
            mLocationClient.connect();
        } else {
            Log.i(TAG, "Going to become invisible");

            mVisible = false;
            stopUpdatingMap();
        }
    }

    private void stopUpdatingMap() {
        if (mLocationClient != null && mLocationClient.isConnected()) {
            mLocationClient.disconnect();
        }
        marker = null;
        markerString = "";
        if (googleMap != null) {
            googleMap.clear();
        }
    }
}