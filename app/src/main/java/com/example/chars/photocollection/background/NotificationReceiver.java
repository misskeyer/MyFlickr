package com.example.chars.photocollection.background;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.chars.photocollection.background.PollService;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "MyNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received result: " + getResultCode());
        if (getResultCode() != Activity.RESULT_OK)
            return;

        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
        Notification notification = intent.getParcelableExtra(PollService.NOTIFICATION);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(requestCode, notification);
    }
}
