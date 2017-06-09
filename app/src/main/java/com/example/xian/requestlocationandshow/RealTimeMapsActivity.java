package com.example.xian.requestlocationandshow;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xian.requestlocationandshow.data.DataManager;
import com.example.xian.requestlocationandshow.firebase.*;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.xian.requestlocationandshow.firebase.singleEvents.AllLocationsOfGroupSingleEvent;
import com.example.xian.requestlocationandshow.firebase.valueevent.RealTimeLocationsEvent;
import com.example.xian.requestlocationandshow.models.Group;
import com.example.xian.requestlocationandshow.models.User;
import com.example.xian.requestlocationandshow.models.UserLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Using location settings.
 * <p/>
 * Uses the {@link com.google.android.gms.location.SettingsApi} to ensure that the device's system
 * settings are properly configured for the app's location needs. When making a request to
 * Location services, the device's system settings may be in a state that prevents the app from
 * obtaining the location data that it needs. For example, GPS or Wi-Fi scanning may be switched
 * off. The {@code SettingsApi} makes it possible to determine if a device's system settings are
 * adequate for the location request, and to optionally invoke a dialog that allows the user to
 * enable the necessary settings.
 * <p/>
 * This sample allows the user to request location updates using the ACCESS_FINE_LOCATION setting
 * (as specified in AndroidManifest.xml). The sample requires that the device has location enabled
 * and set to the "High accuracy" mode. If location is not enabled, or if the location mode does
 * not permit high accuracy determination of location, the activity uses the {@code SettingsApi}
 * to invoke a dialog without requiring the developer to understand which settings are needed for
 * different Location requirements.
 */
public class RealTimeMapsActivity extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    protected static final String TAG = "RealTimeMapsActivity";

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;


    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    private MajorMapController mMajorMapController;
    private List<LatLng> mLocationListOfUsers = new ArrayList<LatLng>();
    private List<User> mNameAndUidListOfUsers = new ArrayList<User>();
    private Group mThisGroup;

    /**
     * UI of buttons and textViews
     */
    private ImageButton but0,but1,but2,but3;
    private TextView tv0,tv1,tv2,tv3;
    private TextView result_mySpeed,result_hisSpeed,
            result_distance,result_suggestSpeed,result_needTime;

    private String mGid = "drink-gid";
    private List<String> mUserIDsOfGroup;

    private AllLocationsOfGroupSingleEvent mLocationsSingleEvent;
    private RealTimeLocationsEvent mRealTimeLocationsEvent;

    private String mMyUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_maps);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        // If onCreate is re-initialized, updnate values.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building the GoogleApiClient, LocationRequest, and
        // LocationSettingsRequest objects.
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_map);
        mMajorMapController = new MajorMapController();
        mapFragment.getMapAsync(mMajorMapController);

        mLocationListOfUsers.add(new LatLng(24.179950, 120.649086));
        mLocationListOfUsers.add(new LatLng(24.179599, 120.649003));
        mLocationListOfUsers.add(new LatLng(24.179300, 120.649100));
        mLocationListOfUsers.add(new LatLng(24.179000, 120.649155));

        if(DataManager.getInstance().isDataEnable()){

            mThisGroup = DataManager.getInstance().getGroupByGid(mGid);

            for (String userId: mThisGroup.members) {

                mNameAndUidListOfUsers.add(DataManager.getInstance().getUserByUid(userId));
            }
        }

        initButtonsAndTextviews();

        mMyUserId = FirebaseAuthHelper.getInstence().getUserID();

        initRealTimeLocationsEvent(mGid);
    }

    // only can set four or less buttons
    private void initButtonsAndTextviews(){

        but0 = (ImageButton) findViewById(R.id.imageButton0);
        but1 = (ImageButton) findViewById(R.id.imageButton1);
        but2 = (ImageButton) findViewById(R.id.imageButton2);
        but3 = (ImageButton) findViewById(R.id.imageButton3);

        tv0 = (TextView)findViewById(R.id.textView0);
        tv1 = (TextView)findViewById(R.id.textView1);
        tv2 = (TextView)findViewById(R.id.textView2);
        tv3 = (TextView)findViewById(R.id.textView3);

        if(DataManager.getInstance().isDataEnable()){

            tv0.setText(mNameAndUidListOfUsers.get(0).username);
            tv1.setText(mNameAndUidListOfUsers.get(1).username);
            tv2.setText(mNameAndUidListOfUsers.get(2).username);
            tv3.setText(mNameAndUidListOfUsers.get(3).username);
        }


        /**
         * set text 型態要有ＮＡＭＥ
         */

        result_mySpeed = (TextView)findViewById(R.id.result_mySpeed);
        result_hisSpeed = (TextView)findViewById(R.id.result_hisSpeed);
        result_distance = (TextView)findViewById(R.id.result_distance);
        result_suggestSpeed = (TextView)findViewById(R.id.result_suggestSpeed);
        result_needTime = (TextView)findViewById(R.id.result_needTime);

        changeTextViewclickListener CTVclickListener = new changeTextViewclickListener();
        but0.setOnClickListener(CTVclickListener);
        but1.setOnClickListener(CTVclickListener);
        but2.setOnClickListener(CTVclickListener);
        but3.setOnClickListener(CTVclickListener);
    }

    private class changeTextViewclickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.imageButton0:
                    result_distance.setText(Float.toString(calculateDistance(mLocationListOfUsers.get(0))));
                    Log.i("RealTimeMapsActivity", "onClick: imBut0");
                    break;

                case R.id.imageButton1:
                    result_distance.setText(Float.toString(calculateDistance(mLocationListOfUsers.get(1))));
                    Log.i("RealTimeMapsActivity", "onClick: imBut1");
                    break;

                case R.id.imageButton2:
                    result_distance.setText(Float.toString(calculateDistance(mLocationListOfUsers.get(2))));
                    Log.i("RealTimeMapsActivity", "onClick: imBut2");
                    break;

                case R.id.imageButton3:
                    result_distance.setText(Float.toString(calculateDistance(mLocationListOfUsers.get(3))));
                    Log.i("RealTimeMapsActivity", "onClick: imBut3");
                    break;

                default:
                    Log.i("RealTimeMapsActivity", "clickListener, onClick: default");
                    break;
            }
        }
    }

    // calculate the distance between I and another user
    private float calculateDistance(LatLng oppositeLatLng){

        float [] results = new float[1];

        Location.distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()
                                    , oppositeLatLng.latitude, oppositeLatLng.longitude, results);
        return results[0];
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {

        // to set locational requestion
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        // set the priority combined with the ACCESS_FINE_LOCATION permission
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateUI();
                        break;
                }
                break;
        }
    }

//    /**
//     * Handles the Start Updates button and requests start of location updates. Does nothing if
//     * updates have already been requested.
//     */
//    private void startUpdatesLocation() {
//        if (!mRequestingLocationUpdates) {
//            mRequestingLocationUpdates = true;
//            //setButtonsEnabledState();
//            startLocationUpdates();
//        }
//    }
//
//    /**
//     * Handles the Stop Updates button, and requests removal of location updates.
//     */
//    private void stopUpdatesLocation() {
//        // It is a good practice to remove location requests when the activity is in a paused or
//        // stopped state. Doing so helps battery performance and is especially
//        // recommended in applications that request frequent location updates.
//        stopLocationUpdates();
//    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, RealTimeMapsActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            status.startResolutionForResult(RealTimeMapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Log.e(TAG, errorMessage);
                        Toast.makeText(RealTimeMapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = false;
                }
                updateUI();
            }
        });

    }

    /**
     * Updates all UI fields.
     */
    private void updateUI() {
        //setButtonsEnabledState();
        updateLocationUI();
    }

    /**
     * Disables both buttons when functionality is disabled due to insuffucient location settings.
     * Otherwise ensures that only one button is enabled at any time. The Start Updates button is
     * enabled if the user is not requesting location updates. The Stop Updates button is enabled
     * if the user is requesting location updates.
     */
//    private void setButtonsEnabledState() {
//        if (mRequestingLocationUpdates) {
//            mStartUpdatesButton.setEnabled(false);
//            mStopUpdatesButton.setEnabled(true);
//        } else {
//            mStartUpdatesButton.setEnabled(true);
//            mStopUpdatesButton.setEnabled(false);
//        }
//    }

    /**
     * Sets the value of the UI fields for the location latitude, longitude and last update time.
     */
    private void updateLocationUI() {

        //for update two buttons
//        if (mCurrentLocation != null) {
//            mLatitudeTextView.setText(String.format("%s: %f", mLatitudeLabel,
//                    mCurrentLocation.getLatitude()));
//            mLongitudeTextView.setText(String.format("%s: %f", mLongitudeLabel,
//                    mCurrentLocation.getLongitude()));
//            mLastUpdateTimeTextView.setText(String.format("%s: %s", mLastUpdateTimeLabel,
//                    mLastUpdateTime));
//        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
                //setButtonsEnabledState();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        mRealTimeLocationsEvent.triggerValueEventListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        mRequestingLocationUpdates = true;
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();

        mRealTimeLocationsEvent.removeValueEventListener();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.

        // first time to connect
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateLocationUI();
        }

        // not the first time to connect
        if (mRequestingLocationUpdates) {
            Log.i(TAG, "in onConnected(), starting location updates");
            startLocationUpdates();
        }

    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
//        mLocationListOfUsers.remove(3);
//        mLocationListOfUsers.add(currentPosition);
//        mMajorMapController.drawLocationPhoto(mLocationListOfUsers);
//        WriteDatabaseHelper.getInstance()
//                .writeRealTimeLocationToGroup("drink_gid",currentPosition);

        updateLocationUI(); // can be deleted
        WriteDatabaseHelper.getInstance().writeRealTimeLocationToGroup(mGid, currentPosition);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initRealTimeLocationsEvent(String gid){

        mRealTimeLocationsEvent = new RealTimeLocationsEvent(gid) {
            @Override
            public void fetchDataFromSnapshot(List<String> uidList,
                                              List<UserLocation> locationList) {

                mLocationListOfUsers.clear();
                for (UserLocation userlocation : locationList) {

                    mLocationListOfUsers.add(userlocation.convertPositionToLatLng());
                }

                mMajorMapController.drawLocationPhoto(mLocationListOfUsers);
            }
        };
    }

}