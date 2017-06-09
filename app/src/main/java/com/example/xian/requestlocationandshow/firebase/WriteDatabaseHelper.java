package com.example.xian.requestlocationandshow.firebase;

import com.example.xian.requestlocationandshow.models.UserLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.xian.requestlocationandshow.firebase.DataBaseContract.LOCATIONS;

/**
 * Created by rick-lee on 2017/6/5.
 */

public class WriteDatabaseHelper {

    public static WriteDatabaseHelper getInstance(){
        return new WriteDatabaseHelper();
    }

    private DatabaseReference firebaseReference;
    private FirebaseUser firebaseUser;
    public String UID;


    private WriteDatabaseHelper(){
        firebaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UID = firebaseUser.getUid();
    }

    public void writeRealTimeLocationToGroup(String gid, LatLng latLng){
        UserLocation userLocation;
        userLocation
                = new UserLocation(latLng.latitude, latLng.longitude, System.currentTimeMillis());

        firebaseReference
                .child(LOCATIONS)
                .child(gid)
                .child(UID)
                .setValue(userLocation);
    }



}
