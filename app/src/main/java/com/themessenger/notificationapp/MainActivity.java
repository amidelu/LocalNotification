package com.themessenger.notificationapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.judemanutd.autostarter.AutoStartPermissionHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    //for shared preferences
    private SharedPreferences myPrefs;
    private static final String PREFS_NAME = "myPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        //checking for specific custom manufacturer
        checkShowAutoStartDialog();

        //setting date and time manually
        setNotification(mContext, "2020-04-04", "01:48");
    }

    public static void setNotification(Context context, String date, String time) {
        //converting string time to date object and setting date object to calendar
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        String todayTime = date + " " + time;

        try {
            Date myTime = sdf.parse(todayTime);
            calendar.setTime(myTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //passing intent to receiver class
        Intent sendToReceiverIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, sendToReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //checking android versions for setting the alarm for notification
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

    //checking and showing dialog for auto start enable option in custom rom mobile phones
    private void checkShowAutoStartDialog() {
        String[] manufacturerList = new String[]{"xiaomi", "huawei", "oppo", "vivo", "honor", "symphony"};
        for (int i = 0; i < manufacturerList.length; i++) {
            if (manufacturerList[i].equalsIgnoreCase(Build.MANUFACTURER)) {
                showDialog();
            }
        }
    }

    //dialog code
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //set the name update layout
        final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        builder.setView(dialogLayout);

        CheckBox checkBox = dialogLayout.findViewById(R.id.checkboxId);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AutoStartPermissionHelper.getInstance().getAutoStartPermission(mContext);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //saving check box status data to shared preferences
                if (buttonView.isChecked()) {
                    storeDialogStatus(true);

                } else {
                    storeDialogStatus(false);
                }
            }
        });
        // checking check box status
        if (getDialogStatus()) {
            dialog.hide();

        } else {
            dialog.show();
        }
    }

    private void storeDialogStatus(boolean isChecked) {
        //updating shared preference checkbox value
        myPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor tipsCheckBox = myPrefs.edit();
        tipsCheckBox.putBoolean("checkBox", isChecked);
        tipsCheckBox.apply();
    }

    //check box status method
    private boolean getDialogStatus() {
        myPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return myPrefs.getBoolean("checkBox", false);
    }
}
