package com.example.xian.requestlocationandshow.firebase.singleEvents;

import android.util.Log;

import com.example.xian.requestlocationandshow.firebase.ValueEventTrigger;
import com.example.xian.requestlocationandshow.models.UserLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.xian.requestlocationandshow.firebase.DataBaseContract.LOCATIONS;

/**
 * Created by rick-lee on 2017/6/6.
 */

public abstract class AllLocationsOfGroupSingleEvent implements ValueEventListener
        , ValueEventTrigger<Map<String, UserLocation>> {

    private static final String TAG = "AllGroupsSingleEvent";
    private String gid;

    public AllLocationsOfGroupSingleEvent(String gid) {
        this.gid = gid;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Map<String, UserLocation>locationMap = new HashMap<>();

        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {

            UserLocation userLocation = locationSnapshot.getValue(UserLocation.class);
            locationMap.put(locationSnapshot.getKey(), userLocation);
        }

        fetchDataFromSnapshot(locationMap);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "load all locations of a group onCancelled: ", databaseError.toException());
    }

    @Override
    public void triggerValueEventListener() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(LOCATIONS)
                .child(gid)
                .addListenerForSingleValueEvent(this);
    }
}
