package com.arirus.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by whd910421 on 16/10/20.
 */

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "notificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (getResultCode() != Activity.RESULT_OK)
            return;

        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
        Notification notification = (Notification) intent.getParcelableExtra(PollService.NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode, notification);
    }
}
