package com.example.xian.requestlocationandshow.firebase.singleEvents;

import android.util.Log;

import com.example.xian.requestlocationandshow.firebase.ValueEventTrigger;
import com.example.xian.requestlocationandshow.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.xian.requestlocationandshow.firebase.DataBaseContract.USERS;

/**
 * Created by rick-lee on 2017/6/3.
 */

public abstract class UserObjectSingleEvent
        implements ValueEventListener, ValueEventTrigger<User> {

    private String uid;
    private String TAG = "SingleEventGetter";

    protected UserObjectSingleEvent(String uid) {
        this.uid = uid;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        fetchDataFromSnapshot(user);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

        Log.w(TAG, "load user onCancelled: ", databaseError.toException());
    }

    @Override
    public void triggerValueEventListener() {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child(USERS)
                .child(uid)
                .addListenerForSingleValueEvent(this);
    }

}
