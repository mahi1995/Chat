package com.mahe.chat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;


import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Home extends AppCompatActivity {


    //private FirebaseListAdapter<ChatMessage> adapter;
    PubNub  mPubnub_DataStream;


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        setUpFragments();




        //btnSend = (Button) findViewById(R.id.btnsend);






        PNConfiguration config = new PNConfiguration();
        config.setPublishKey(ConstantsCollection.PUBLISH_KEY);
        config.setSubscribeKey(ConstantsCollection.SUBSCRIBE_KEY);
        config.setUuid(ConstantsCollection.UUID);
        config.setSecure(true);
        this.mPubnub_DataStream = new PubNub(config);

        mPubnub_DataStream.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                Toast.makeText(getApplicationContext(),""+status.getCategory(),Toast.LENGTH_LONG).show();
                if(status.getCategory()== PNStatusCategory.PNConnectedCategory){
                    mPubnub_DataStream.publish().channel(ConstantsCollection.CHANNEL).message("{msg:hello}").async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            // Toast.makeText(getApplicationContext(), status.getCategory()+"",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void message(PubNub pubnub, final PNMessageResult message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"rcvd ="+message.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });



/*
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPubnub_DataStream.subscribe().channels(Arrays.asList(ConstantsCollection.CHANNEL)).execute();
            }
        });*/




    }

void setUpFragments(){
    viewPager = (ViewPager) findViewById(R.id.viewpager);

    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

    adapter.addFragment(new ChatFragment(), "Chats");
    adapter.addFragment(new ContactFragment(), "Contacts");
    viewPager.setAdapter(adapter);


    tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(viewPager);
}










}
