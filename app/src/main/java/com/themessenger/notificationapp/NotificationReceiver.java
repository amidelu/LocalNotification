package com.themessenger.notificationapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by amidelu on 4/3/20
 **/
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        NotificationService.enqueueWork(context, serviceIntent);
    }
}
