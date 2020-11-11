package com.bharatbloodbank.bharatbloodbank.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.bharatbloodbank.bharatbloodbank.R;


public class NotificationHelper extends ContextWrapper {
    private static final String CHANAL_ID = " com.foodit.bbdonation.Helper";
    private static final String CHANAL_NAME = "Blood Donation";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel eatitChannel = new NotificationChannel(CHANAL_ID,
                CHANAL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        eatitChannel.enableLights(false);
        eatitChannel.enableVibration(true);
        eatitChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(eatitChannel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getBBDChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundUri) {
        return new Notification.Builder(getApplicationContext(), CHANAL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(soundUri)
                .setAutoCancel(true);
    }

 /*   @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getEatItChannelNotification(String title, String body, Uri soundUri) {
        return new Notification.Builder(getApplicationContext(), CHANAL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(soundUri)
                .setAutoCancel(false);
    }*/

}
