package com.example.chars.photocollection;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.chars.photocollection.data.PhotoItem;

import java.util.List;

public class PollService extends IntentService {
    private static final String TAG = "PollService";
    private static final String CHANNEL_ID = "channel_pollservice";
    private static final int POLL_INTERVAL = 1000 * 60;

    public PollService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Log.i(TAG,"Set Alarm.");
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0, i,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn)
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0, i,
                PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected())
            return;

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<PhotoItem> items;

        if (query == null)
            items = new FlickrFetchr().fecthRecentPhotos();
        else
            items = new FlickrFetchr().searchPhotos(query);

        if (items.size() == 0)
            return;

        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId))
            Log.i(TAG,"Got an old result:" + resultId);
        else {
            Log.i(TAG,"Got a new result:" + resultId);

            Resources resources = getResources();
            Intent i = photoCollectionActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification notification = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(CHANNEL_ID, TAG,
                    NotificationManagerCompat.IMPORTANCE_LOW);
            Log.i(TAG,"Channel created.");
            manager.createNotificationChannel(channel);

            notification = new NotificationCompat.Builder(this , CHANNEL_ID)
                    .setTicker(resources.getString(R.string.new_photos_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_photos_title))
                    .setContentText(resources.getString(R.string.new_photos_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            } else {
                notification = new NotificationCompat.Builder(this)
                        .setTicker(resources.getString(R.string.new_photos_title))
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle(resources.getString(R.string.new_photos_title))
                        .setContentText(resources.getString(R.string.new_photos_text))
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();
            }

            manager.notify(0, notification);
        }
        QueryPreferences.setLastResultId(this, resultId);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
