package com.nickrman.notificationsample;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

public class MainActivity extends AppCompatActivity {
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        JodaTimeAndroid.init(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = "NotificationSampleId";
            String channelName = "Notification Sample";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        JodaTimeAndroid.init(this);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        findViewById(R.id.alarm_btn).setOnClickListener(v -> {
            DateTime todayWithSelectedTime = DateTime.now().plusSeconds(30);
            DateTime now = DateTime.now();
            if (todayWithSelectedTime.isAfter(DateTime.now())) {
                long delay = todayWithSelectedTime.getMillis() - now.getMillis();
                Log.d(MainActivity.class.getSimpleName(), "Future!! Could be scheduled");
                Intent intent = new Intent(MainActivity.this, NotificationPublisher.class);
                intent.putExtra(NotificationPublisher.NOTIFICATION_ID, 42);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime() + delay, pendingIntent);
            }
        });
    }
}
