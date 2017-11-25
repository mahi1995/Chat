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
import android.os.Handler;
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
import android.util.Log;
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
    MyDB db;
    boolean isMymsg=false;
     ChatFragment ch1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i=new Intent(this,PubNubService.class);
        bindService(i, serviceConnection, BIND_AUTO_CREATE);
        db=new MyDB(this);
        setUpFragments();

    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pubNubService=((PubNubService.LocalBinder) service).getBinder();
            pubNubService.sendPush();

            pubNubService.subscribeForChannels(new Messages.MessageReceived() {
                @Override
                public void onReceive(final String s) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                JSONObject jsonObject=new JSONObject();

                                for (String temp:s.split(",")) {
                                    temp=temp.replace("\"","").replace("{","").replace("}","");

                                    temp=temp.replace("\\","");
                                    String t[]=temp.split(":");
                                    if(t[0].contains("from")){
                                        jsonObject.put("from",t[1]);
                                    }else if(t[0].contains("time")){
                                        jsonObject.put("time",temp.replace("time:","").replace("}",""));
                                    }else
                                        jsonObject.put(t[0],t[1]);

                                }
                                Message m= new Message(
                                        jsonObject.getString("from"),
                                        jsonObject.getString("message"),
                                        jsonObject.getString("time"),
                                        0,
                                        Boolean.parseBoolean(jsonObject.getString("isControlMessage"))
                                );
                                if(m.isControlMessage()){
                                   long i= db.addChannel(m.getFrom());
                                    Toast.makeText(getApplicationContext(),i+"",Toast.LENGTH_LONG).show();
                                    db.insertChat(m);
                                    pubNubService.getPubNub().subscribe().channels(db.getAllChannels()).execute();

                                }else {
                                    if(m.getFrom().length()>15){

                                        String t=m.getFrom().substring(0,m.getFrom().indexOf("@@@"));
                                        m.setFrom(m.getFrom().substring(m.getFrom().indexOf("@@@")+3));
                                        m.setTime(m.getTime()+"\n"+t);
                                        if(t.equals(db.getAllChannels().get(0)))
                                        {
                                            isMymsg=true;
                                        }else {
                                            db.insertChat(m);
                                            isMymsg=false;
                                        }

                                    }else
                                        db.insertChat(m);
                                }

                               // Toast.makeText(getApplicationContext(),"From="+m.getFrom(),Toast.LENGTH_LONG).show();

                               // Toast.makeText(getApplicationContext(),m.getMessage(),Toast.LENGTH_LONG).show();


                                try {
                                    ChatFragmentRecyclerAdapter adp=new ChatFragmentRecyclerAdapter(db.getDistinctChats(),getApplicationContext());
                                    ch1.recyclerView.setAdapter(adp);
                                }catch (Exception e){}



                                NotificationManager notificationManager = (NotificationManager)
                                        getSystemService(NOTIFICATION_SERVICE);
                                Intent intent = new Intent(getApplicationContext(), Messages.class);
                                intent.putExtra("phone",m.getFrom());
                                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);

                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(getApplicationContext())
                                                .setSmallIcon(R.drawable.chat)
                                                .setContentTitle("New Message")
                                                .setContentText("From : "+m.getFrom())
                                                .setAutoCancel(true)
                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                        .setContentIntent(pIntent);
                                if(!isMymsg)
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    void setUpFragments(){
    viewPager = (ViewPager) findViewById(R.id.viewpager);

    final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
       ch1=new ChatFragment();
    adapter.addFragment(ch1, "Chats");
    adapter.addFragment(new ContactFragment(), "Contacts");
    viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){

                    try {
                        ChatFragmentRecyclerAdapter adp=new ChatFragmentRecyclerAdapter(db.getDistinctChats(),getApplicationContext());
                        ch1.recyclerView.setAdapter(adp);
                    }catch (Exception e){}
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(viewPager);
}
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {


            super.onBackPressed();
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}
