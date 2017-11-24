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
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by hp on 24-11-2017.
 */

public class PubNubService extends Service {
    PubNub  mPubnub_DataStream;
    String m;
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
        return  mPubnub_DataStream;
    }


    void subscribeForChannels(final Messages.MessageReceived messageReceived){

        getPubNub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if(status.getCategory()== PNStatusCategory.PNConnectedCategory){
                    //Toast.makeText(getApplicationContext(),"status="+status.getCategory().toString(),Toast.LENGTH_LONG).show();

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

        getPubNub().subscribe().channels(Arrays.asList(ConstantsCollection.CHANNEL)).execute();
    }

    void publishMessage(String channel,Message message){
        try {
            Gson gson=new Gson();
            JSONObject jsonObject=new JSONObject(gson.toJson(message));
            getPubNub().publish().channel(channel).message(jsonObject.toString()).async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                           // Toast.makeText(getApplicationContext(),status.getCategory()+"",Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
