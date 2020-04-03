package com.themessenger.notificationapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

/**
 * Created by amidelu on 4/3/20
 **/

public class NotificationService extends JobIntentService {

    private static final int JOB_ID = 1000;
    private int currentNotificationId = 1;
    String CHANNEL_ID = "my_channel_id_01";
    public static int REQUEST_CODE = 100;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotificationService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        setNotification();
    }

    //set notification
    private void setNotification() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My Notification", NotificationManager.IMPORTANCE_HIGH);

            //configuring the notification channel
            notificationChannel.setDescription("Channel Description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[] {0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.right)
                .setTicker("Hearty365")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("This is title")
                .setContentText("This is body text")
                .setContentInfo("Info");

        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(currentNotificationId, notificationBuilder.build());
    }
}
