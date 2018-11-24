package com.nickrman.notificationsample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String message = "";
        if (intent.getExtras().containsKey(FCMService.MESSAGE)) {
           message = intent.getStringExtra(FCMService.MESSAGE);
        }
        message = message.isEmpty() ? "Wake up" : message;
        int id = intent.getIntExtra(NOTIFICATION_ID, 0 );
        Intent i = new Intent(context, MapsActivity.class);
        i.putExtras(intent.getExtras());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , i, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = getNotification(message, notificationManager, context)
                .setContentIntent(pendingIntent).build();
        notificationManager.notify(id, notification);
    }

    private Notification.Builder getNotification(String content, NotificationManager notificationManager, Context context) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setAutoCancel(true);
        builder.setSound(alarmSound);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "NotificationSampleId";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Notification Sample",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        return builder;
    }
}
