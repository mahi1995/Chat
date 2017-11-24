package com.mahe.chat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
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


import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Home extends AppCompatActivity {

    PubNubService pubNubService;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i=new Intent(this,PubNubService.class);
        bindService(i, serviceConnection, BIND_AUTO_CREATE);


        setUpFragments();

    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pubNubService=((PubNubService.LocalBinder) service).getBinder();
            pubNubService.subscribeForChannels(new Messages.MessageReceived() {
                @Override
                public void onReceive(final String s) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                JSONObject jsonObject=new JSONObject();
                                for (String temp:s.split(",")) {
                                    temp=temp.replace("\"","");

                                    temp=temp.replace("\\","");
                                    String t[]=temp.split(":");
                                    jsonObject.put(t[0],t[1]);

                                }

                                Gson gson=new Gson();
                                Message m= gson.fromJson(new JsonParser().parse(jsonObject.toString()),Message.class);
                                m.setIsMe(0);
                                Toast.makeText(getApplicationContext(),m.getMessage(),Toast.LENGTH_LONG).show();



                                NotificationManager notificationManager = (NotificationManager)
                                        getSystemService(NOTIFICATION_SERVICE);
                                Intent intent = new Intent(getApplicationContext(), Messages.class);
                                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);

                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(getApplicationContext())
                                                .setSmallIcon(R.drawable.in_message)
                                                .setContentTitle("My notification")
                                                .setContentText("Hello World!")
                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                                        .setContentIntent(pIntent);
                                notificationManager.notify(1,mBuilder.build());







                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };





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
