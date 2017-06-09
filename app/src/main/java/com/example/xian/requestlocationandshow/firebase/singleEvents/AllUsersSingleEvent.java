package com.example.xian.requestlocationandshow.firebase.singleEvents;

import android.util.Log;

import com.example.xian.requestlocationandshow.firebase.ValueEventTrigger;
import com.example.xian.requestlocationandshow.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.xian.requestlocationandshow.firebase.DataBaseContract.USERS;

/**
 * Created by rick-lee on 2017/6/6.
 */

public abstract class AllUsersSingleEvent
        implements ValueEventListener
        , ValueEventTrigger<Map<String, User>> {

    private static final String TAG = "AllUsersSingleEvent";

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Map<String, User>users = new HashMap<>();

        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
            users.put(userSnapshot.getKey(), userSnapshot.getValue(User.class));
        }

        fetchDataFromSnapshot(users);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "load all users onCancelled: ", databaseError.toException());
    }

    @Override
    public void triggerValueEventListener() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS)
                .addListenerForSingleValueEvent(this);
    }
}
