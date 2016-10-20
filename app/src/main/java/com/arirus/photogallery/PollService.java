package com.arirus.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.List;

/**
 * Created by whd910421 on 16/10/19.
 */

public class PollService extends IntentService {
    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000*60;
    public static final String ACTION_SHOW_NOTIFICATION = "com.arirus.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.arirus.photogallery.PRIVATE";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    public static Intent newIntent(Context context)
    {
        return new Intent(context, PollService.class);
    }

    public static boolean isServerAlarmOn(Context context)
    {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public static void setServiceAlarm(Context context,boolean isOn)
    {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isOn)
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),POLL_INTERVAL,pi);
        else
        {
            alarmManager.cancel(pi);
            pi.cancel();
        }
        QueryPreferences.setAlarmOn(context, isOn);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (!isNetWorkAvailableAndConnected())
            return;
        arirusLog.get().ShowLog(TAG, "received an intent ",intent.toString());
        String query = QueryPreferences.getStoredQUery(this);
        String lastResultID = QueryPreferences.getLastResultID(this);
        List<GalleryItem> items;

        if (query == null)
            items = new FlickrFetchr().fetchRecentPhotos();
        else
            items = new FlickrFetchr().searchPhotos(query);

        String resultID = items.get(0).getId();
        arirusLog.get().ShowLog(TAG, lastResultID,"@@@",resultID);
        if (resultID.equals(lastResultID))
            arirusLog.get().ShowLog(TAG,"旧的ID");
        else {
            arirusLog.get().ShowLog(TAG, "新的ID");
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this,0,i,0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(("New Gallery Photos"))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle("New Gallery Photos")
                    .setContentText("You have new pictures in GalleryPhotos")
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            showBackgroundNotification(0, notification);
        }
        QueryPreferences.setLastResultID(this, resultID);

    }

    private void showBackgroundNotification(int requestcode, Notification notification)
    {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestcode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i,PERM_PRIVATE,null,null, Activity.RESULT_OK,null,null);
    }

    private boolean isNetWorkAvailableAndConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetWorkAvailable = cm.getActiveNetworkInfo()!=null;
        boolean isNetWorkConnected = isNetWorkAvailable&& cm.getActiveNetworkInfo().isConnected();
        return isNetWorkConnected;
    }
}
