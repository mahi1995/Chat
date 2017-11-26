package com.mahe.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.ObjectConstructor;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by hp on 24-11-2017.
 */

public class PubNubService extends Service {
    PubNub  mPubnub_DataStream;
    String m;
    MyDB db;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    private IBinder iBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        PubNubService getBinder() {
            return PubNubService.this;
        }
    }


    PubNub getPubNub(){
        if(mPubnub_DataStream==null){
            PNConfiguration config = new PNConfiguration();
            config.setPublishKey(ConstantsCollection.PUBLISH_KEY);
            config.setSubscribeKey(ConstantsCollection.SUBSCRIBE_KEY);
            config.setUuid(ConstantsCollection.UUID);
            config.setSecure(true);

            this.mPubnub_DataStream = new PubNub(config);
        }
        db=new MyDB(getApplicationContext());
        return  mPubnub_DataStream;
    }


    void subscribeForChannels(final Home.MessageReceived messageReceived){

        getPubNub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
               // Toast.makeText(getApplicationContext(),"status="+status.getCategory().toString(),Toast.LENGTH_LONG).show();
                if(status.getCategory()== PNStatusCategory.PNConnectedCategory){

                }
            }

            @Override
            public void message(PubNub pubnub, final PNMessageResult message) {
                messageReceived.onReceive(message.getMessage().toString());
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });


        getPubNub().subscribe().channels(db.getAllChannels()).execute();
    }

    void publishMessage(String channel,Message message){
        try {
            Gson gson=new Gson();
            final JSONObject jsonObject=new JSONObject(gson.toJson(message));

            getPubNub().publish().channel(channel).message(jsonObject.toString()).async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                           //Toast.makeText(getApplicationContext(),status.getCategory()+"",Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void publishMessageAll(List<String> channels, Message message){
        try {
            Gson gson=new Gson();
            final JSONObject jsonObject=new JSONObject(gson.toJson(message));
            for (String channel:channels) {
                try {
                    getPubNub().publish().channel(channel).message(jsonObject.toString()).async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            //Log.i("json==", jsonObject.toString());
                            //Toast.makeText(getApplicationContext(),status.getCategory()+"",Toast.LENGTH_LONG).show();
                        }
                    });
                }catch (Exception ex){}
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void sendPush(){
        getPubNub().addPushNotificationsOnChannels()
                .deviceId("com.mahe.chat")
                .channels(db.getAllChannels())
                .pushType(PNPushType.GCM)
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                       // Toast.makeText(getApplicationContext(),status.getCategory()+"",Toast.LENGTH_LONG).show();
                    }
                });

    }

}
