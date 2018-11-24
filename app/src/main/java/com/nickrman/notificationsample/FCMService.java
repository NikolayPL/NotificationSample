package com.nickrman.notificationsample;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {
    public static final String TAG = FCMService.class.getSimpleName();
    public static final String MESSAGE = "Message";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG,remoteMessage.getFrom());


        Intent intent = new Intent(getApplicationContext(), NotificationPublisher.class);
        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, 42);
        intent.putExtra(MESSAGE, remoteMessage.getData().get(0));
        getApplicationContext().sendBroadcast(intent);

    }
}
