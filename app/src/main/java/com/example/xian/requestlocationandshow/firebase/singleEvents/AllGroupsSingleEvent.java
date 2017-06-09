package com.example.xian.requestlocationandshow.firebase.singleEvents;

import android.util.Log;

import com.example.xian.requestlocationandshow.firebase.ValueEventTrigger;
import com.example.xian.requestlocationandshow.models.Group;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.xian.requestlocationandshow.firebase.DataBaseContract.GROUPS;

/**
 * Created by rick-lee on 2017/6/6.
 */

public abstract class AllGroupsSingleEvent
        implements ValueEventListener
        , ValueEventTrigger<Map<String, Group>> {

    private static final String TAG = "AllGroupsSingleEvent";

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Map<String, Group>groups = new HashMap<>();

        for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
            groups.put(groupSnapshot.getKey(), groupSnapshot.getValue(Group.class));
        }

        fetchDataFromSnapshot(groups);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "load all groups onCancelled: ", databaseError.toException());
    }

    @Override
    public void triggerValueEventListener() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(GROUPS)
                .addListenerForSingleValueEvent(this);
    }
}
