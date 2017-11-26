package com.mahe.chat;

import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by hp on 26-11-2017.
 */

public class MyFireBaseMessagingervice extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Toast.makeText(getApplicationContext(),"received",Toast.LENGTH_LONG).show();
    }
}
