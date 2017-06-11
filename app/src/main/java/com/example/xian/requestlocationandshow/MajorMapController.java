package com.example.xian.requestlocationandshow;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2017/6/4.
 */

public class MajorMapController implements OnMapReadyCallback {

    GoogleMap mMap;
    boolean isInitial = false;

    private final List<BitmapDescriptor> mImages = new ArrayList<BitmapDescriptor>();

    private List<GroundOverlay> ListOfUserGOl = new ArrayList<GroundOverlay>();
    private List<GroundOverlay> ListOfWarnGOl = new ArrayList<GroundOverlay>();
    private List<LatLng> mLocationListOfTrackLine;

    int[] imagedID = {R.drawable.zero, R.drawable.one, R.drawable.two, R.drawable.three,
                        R.drawable.four, R.drawable.five, R.drawable.six, R.drawable.seven,
                        R.drawable.eight, R.drawable.nine, R.drawable.ten};


//    private List<Integer> ListOfImageReference = new ArrayList<Integer>();

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //camera初始化地理位置
        LatLng initCamPosition = new LatLng(24.179734, 120.648635);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initCamPosition, 18f));

        if(mLocationListOfTrackLine != null)
            drawPolyLine();

    }

    public void setmLocationListOfTrackLine(List<LatLng> mLocationListOfTrackLine) {
        this.mLocationListOfTrackLine = mLocationListOfTrackLine;
    }

    public void setFullListOfWarnGOlAsTransparent(){

        for (GroundOverlay groudOverlay: ListOfWarnGOl) {

            groudOverlay.setVisible(false);
        }
    }

    public void setListOfWarnGOlAsVisible(boolean[] needSetListOfUserInt){

        for (int i = 0; i < needSetListOfUserInt.length; i++) {

            if (needSetListOfUserInt[i])
            {
                Log.i("MajorMapController", "setListOfWarnGOlAsVisible: i = " + i);
                ListOfWarnGOl.get(i).setVisible(true);
            }

        }
    }

    private void initUserCircleGroudOverlay (List<LatLng> LocationListOfUsers){

        for(int i = 0; i < LocationListOfUsers.size(); i++){

            GroundOverlayOptions warnGroundOverlayOptions = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.red_warning))
                    .position(LocationListOfUsers.get(i), 30f, 30f)
                    .visible(false);

            GroundOverlayOptions userGroundOverlayOptions = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(imagedID[i]))
                    .position(LocationListOfUsers.get(i), 10f, 10f)
                    .visible(true);

            ListOfWarnGOl.add(mMap.addGroundOverlay(warnGroundOverlayOptions));

            ListOfUserGOl.add(mMap.addGroundOverlay(userGroundOverlayOptions));
        }

        isInitial = true;
    }

    public void drawLocationPhoto(List<LatLng> LocationListOfUsers){

        if(isInitial){

            for (int i = 0; i < LocationListOfUsers.size(); i++) {

                LatLng position = LocationListOfUsers.get(i);
                ListOfUserGOl.get(i).setPosition(position);
                ListOfWarnGOl.get(i).setPosition(position);

            }
        }

        else
            initUserCircleGroudOverlay(LocationListOfUsers);

    }

    private void drawPolyLine(){

        if(mLocationListOfTrackLine == null)
            Log.i("MajorMapController", "drawPolyLine: List is null");

        else
            Log.i("MajorMapController", "drawPolyLine: List is not null");

        PolylineOptions lienOptions = new PolylineOptions()
                .addAll(mLocationListOfTrackLine)
                .color(Color.parseColor("#54a2de"))
                .width(50f);

        if(mMap == null)
            Log.i("MajorMapController", "drawPolyLine: mMap is null");

        else
            Log.i("MajorMapController", "drawPolyLine: mMap is not null");

            mMap.addPolyline(lienOptions);
    }

    GoogleMap getGoogleMap(){
        return this.mMap;
    }
}
