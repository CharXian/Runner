package com.example.xian.requestlocationandshow.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by rick-lee on 2017/6/6.
 */

public class UserLocation {

    public double latitude;
    public double longitude;
    public Long timestamp;

    public UserLocation() {}

    public UserLocation(double latitude, double longitude, Long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public LatLng convertPositionToLatLng(){
        return new LatLng(latitude, longitude);
    }
}
