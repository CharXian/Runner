/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.xian.requestlocationandshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.xian.requestlocationandshow.data.DataManager;
import com.example.xian.requestlocationandshow.firebase.singleEvents.AllGroupsSingleEvent;
import com.example.xian.requestlocationandshow.firebase.singleEvents.AllUsersSingleEvent;
import com.example.xian.requestlocationandshow.models.Group;
import com.example.xian.requestlocationandshow.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";

    public static final String USERS_NODE = "users";

    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    private AllUsersSingleEvent mAllUsersSingleEvent = new AllUsersSingleEvent() {
        @Override
        public void fetchDataFromSnapshot(Map<String, User> data) {
            DataManager.getInstance().setAllUsersData(data);
        }
    };

    private AllGroupsSingleEvent mAllGroupsSingleEvent = new AllGroupsSingleEvent() {
        @Override
        public void fetchDataFromSnapshot(Map<String, Group> data) {
            DataManager.getInstance().setAllGroupsData(data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAllUsersSingleEvent.triggerValueEventListener();
        mAllGroupsSingleEvent.triggerValueEventListener();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;
/*STEP6身分驗證----檢查當前用戶，每當打開應用程序並且未經身份驗證時將用戶發送到登錄屏幕----------*/
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

/*-------------------------------------------------------------------------------------*/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


/*STEP6身分驗證-------------------------------------------------------------------*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
/*STEP6身分驗證---添加一個新的案例來處理退出按鈕------------------------------------*/
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                mPhotoUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
                return true;

            case R.id.test_load_data_menu:

                String title;
                boolean enable = DataManager.getInstance().isDataEnable();
                if (enable)
                    title = "Data enable to access!";
                else
                    title = "Data loading...";

                Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
                return true;

            case R.id.running_menu:
                startActivity(new Intent(this, RealTimeMapsActivity.class));
            return true;



/*---------default也是STEP6-----------------------------------------------------*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

}
