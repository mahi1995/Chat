package com.mahe.chat;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mahe.chat.ConstantsCollection;
import com.mahe.chat.MyDB;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;

/**
 * Created by hp on 26-11-2017.
 */

public class MyFireBaseInstantIdService extends FirebaseInstanceIdService {
    String refreshedToken;
    PubNub pubNub;
    MyDB db;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("MyToken=",  refreshedToken);
        getPubNub().addPushNotificationsOnChannels()
                .deviceId(refreshedToken)
                .channels(db.getAllChannels())
                .pushType(PNPushType.GCM)
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                        // Toast.makeText(getApplicationContext(),status.getCategory()+"",Toast.LENGTH_LONG).show();
                        Log.i("publishresult",result.toString());

                    }
                });

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
    PubNub getPubNub(){
        if(pubNub==null){
            PNConfiguration config = new PNConfiguration();
            config.setPublishKey(ConstantsCollection.PUBLISH_KEY);
            config.setSubscribeKey(ConstantsCollection.SUBSCRIBE_KEY);
            config.setUuid(ConstantsCollection.UUID);
            config.setSecure(true);

            this.pubNub = new PubNub(config);
        }
        db=new MyDB(getApplicationContext());
        return  pubNub;
    }
}
