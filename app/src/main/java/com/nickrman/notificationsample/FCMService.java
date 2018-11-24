package com.nickrman.notificationsample;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

public class FCMService extends FirebaseMessagingService {
    public static final String TAG = FCMService.class.getSimpleName();
    public static final String MESSAGE = "Message";
    public static final String LAT = "lat";
    public static final String LONG = "long";
    public static final String RADIUS = "radius";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG,remoteMessage.getFrom());


        Intent intent = new Intent(getApplicationContext(), NotificationPublisher.class);
        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, 42);
            remoteMessage.getData();
        intent.putExtra(LAT, remoteMessage.getData().get(LAT));
        intent.putExtra(LONG,remoteMessage.getData().get(LONG));
        intent.putExtra(RADIUS,remoteMessage.getData().get(RADIUS));
        getApplicationContext().sendBroadcast(intent);

    }
}
