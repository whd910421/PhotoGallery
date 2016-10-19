package com.arirus.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;

import java.util.List;

/**
 * Created by whd910421 on 16/10/19.
 */

public class PollService extends IntentService {
    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000*60;

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
        else
            arirusLog.get().ShowLog(TAG, "新的ID");

        QueryPreferences.setLastResultID(this, resultID);

    }

    private boolean isNetWorkAvailableAndConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetWorkAvailable = cm.getActiveNetworkInfo()!=null;
        boolean isNetWorkConnected = isNetWorkAvailable&& cm.getActiveNetworkInfo().isConnected();
        return isNetWorkConnected;
    }
}
