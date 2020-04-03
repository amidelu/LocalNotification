package com.themessenger.notificationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        setNotification(mContext, "2020-04-03", "20:8");
    }

    public static void setNotification(Context context, String date, String time) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        String todayTime = date + " " + time;

        try {
            Date myTime = sdf.parse(todayTime);
            calendar.setTime(myTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent sendToReceiverIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, sendToReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT < Build.VERSION_CODES.KITKAT) {
            assert alarmManager != null;
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        } else if (SDK_INT >= Build.VERSION_CODES.KITKAT && SDK_INT < Build.VERSION_CODES.M) {
            assert alarmManager != null;
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            assert alarmManager != null;
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

    }
}
