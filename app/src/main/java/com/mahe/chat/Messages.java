package com.mahe.chat;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Messages extends AppCompatActivity {

    RecyclerView rc;
    EditText txtMessage;
    Button btnSend;
    ArrayList<Message> l;
    private Toolbar toolbar;
    PubNubService pubNubService;
    boolean isBoubd=false;
    String phone;
     MessagesRecyclerAdapter adp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        rc=(RecyclerView)findViewById(R.id.recyclerMessages);
        btnSend=(Button)findViewById(R.id.btnmessagesSend);
        txtMessage=(EditText)findViewById(R.id.txtMessagesEdit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        phone=getIntent().getStringExtra("phone");
        if(phone.isEmpty()) {
            finish();
            startActivity(new Intent(this,Home.class));
        }
        getSupportActionBar().setTitle(phone);
        toolbar.setTitleTextColor(Color.WHITE);

        pubNubService=new PubNubService();
        Intent i=new Intent(this,PubNubService.class);
            bindService(i, serviceConnection, BIND_AUTO_CREATE);

         l=new ArrayList<>();

         adp=new MessagesRecyclerAdapter(l,this);
        rc.setLayoutManager(new LinearLayoutManager(this));
        rc.setAdapter(adp);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtMessage.getText().toString().isEmpty()&&isBoubd){
                    Message m=new Message(ConstantsCollection.CHANNEL,txtMessage.getText().toString(), DateFormat.getDateTimeInstance().format(new Date()).toString(),1);
                    pubNubService.publishMessage(phone,m);
                    adp.add(adp.getItemCount(),m);
                    txtMessage.setText("");

                }
            }
        });




    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pubNubService=((PubNubService.LocalBinder) service).getBinder();
            isBoubd=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBoubd=false;
        }
    };


    interface MessageReceived {
        void onReceive(String s);

    }


}
