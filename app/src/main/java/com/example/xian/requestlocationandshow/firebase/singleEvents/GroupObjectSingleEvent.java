package com.example.xian.requestlocationandshow.firebase.singleEvents;


import android.util.Log;

import com.example.xian.requestlocationandshow.firebase.ValueEventTrigger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by rick-lee on 2017/6/6.
 */

public abstract class GroupObjectSingleEvent
        implements ValueEventListener, ValueEventTrigger<List<String>> {

    private String gid;
    private static final String TAG = "GroupObjectSingleEvent";

    public GroupObjectSingleEvent(String gid) {
        this.gid = gid;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {


//        fetchDataFromSnapshot();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "load group onCancelled: ", databaseError.toException());
    }

    @Override
    public void triggerValueEventListener() {

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child(gid)
                .addListenerForSingleValueEvent(this);
    }
}
