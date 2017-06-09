package com.example.xian.requestlocationandshow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MessageActivity extends AppCompatActivity {

    //for sending message or Image Functions
    private Button mSendButton;
    private EditText mMesssgeEditText;
    private ImageView mAddMessageImageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


    }

    private void initSendingMessageOrImageFunctions(){

        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



}
