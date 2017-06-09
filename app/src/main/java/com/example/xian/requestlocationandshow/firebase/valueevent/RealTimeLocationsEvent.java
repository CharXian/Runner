package com.example.xian.requestlocationandshow.firebase.valueevent;


import android.util.Log;

import com.example.xian.requestlocationandshow.models.UserLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.xian.requestlocationandshow.firebase.DataBaseContract.LOCATIONS;


/**
 * Created by rick-lee on 2017/6/6.
 */

public abstract class RealTimeLocationsEvent
        implements ValueEventListener {

    private DatabaseReference locatonGroupReference;
    private String gid;
    public static final String TAG = "RealTimeLocationsEvent";

    public RealTimeLocationsEvent(String gid) {
        this.gid = gid;
        locatonGroupReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(LOCATIONS)
                .child(gid);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        List<String> uidList = new ArrayList<>();
        List<UserLocation> locationList = new ArrayList<>();

        for (DataSnapshot childSnapshot:dataSnapshot.getChildren()) {

            String uid = childSnapshot.getKey();
            UserLocation userLocation = childSnapshot.getValue(UserLocation.class);

            uidList.add(uid);
            locationList.add(userLocation);
        }

        fetchDataFromSnapshot(uidList, locationList);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, " load real-time location onCancelled: ", databaseError.toException());
    }

    public abstract void fetchDataFromSnapshot(List<String> uidList,
                                               List<UserLocation> locationList);

    public void triggerValueEventListener(){
        locatonGroupReference.addValueEventListener(this);
    }
    public void removeValueEventListener(){
        locatonGroupReference.removeEventListener(this);
    }


}
