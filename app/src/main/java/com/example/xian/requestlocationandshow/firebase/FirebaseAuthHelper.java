package com.example.xian.requestlocationandshow.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by rick-lee on 2017/6/6.
 */

public class FirebaseAuthHelper {

    static public FirebaseAuthHelper getInstence(){return new FirebaseAuthHelper();}

    private FirebaseUser firebaseUser;

    public FirebaseAuthHelper(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getUserID(){
        return firebaseUser.getUid();
    }
}
