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

import com.philliphsu.numberpadtimepicker.BottomSheetNumberPadTimePickerDialog;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

public class MainActivity extends AppCompatActivity {
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        JodaTimeAndroid.init(this);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        findViewById(R.id.alarm_btn).setOnClickListener(v -> {
            BottomSheetNumberPadTimePickerDialog dialog = new BottomSheetNumberPadTimePickerDialog(
                    this, (view, hourOfDay, minutes) -> {
                Log.d(MainActivity.class.getSimpleName(), "Time " + hourOfDay + ":" + minutes);
                DateTime todayWithSelectedTime = DateTime.now().withHourOfDay(hourOfDay).withMinuteOfHour(minutes).withSecondOfMinute(0);
                DateTime now = DateTime.now();
                if (todayWithSelectedTime.isAfter(DateTime.now())) {
                    long delay = todayWithSelectedTime.getMillis() - now.getMillis();
                    Log.d(MainActivity.class.getSimpleName(), "Future!! Could be scheduled");
                    Intent intent = new Intent(MainActivity.this, NotificationPublisher.class);
                    intent.putExtra(NotificationPublisher.NOTIFICATION_ID, 42);
                    intent.putExtra(NotificationPublisher.NOTIFICATION, getNotification("WAKE UP"));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime() + delay, pendingIntent);
                }
            }
                    , true
            );
            dialog.show();
        });
    }


    private Notification getNotification(String context) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(context);
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

        return builder.build();
    }
}
