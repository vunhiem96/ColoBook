package com.volio.coloringbook2.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.volio.coloringbook2.R;
import com.volio.coloringbook2.activity.MainActivity;

public class MyFirebaseMessageService extends FirebaseMessagingService {

    public static int NOTIFYCATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        generateNotifycation(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());

    }

    private void generateNotifycation(String body, String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifyBuilder;

        notifyBuilder = new NotificationCompat.Builder(this, "chanel_1")
                .setSmallIcon(R.drawable.ic_splash)
                .setContentText(body)
                .setContentTitle(title)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (NOTIFYCATION_ID > 1073741824) {
            NOTIFYCATION_ID = 0;
        }
        if (notificationManager != null) {
            notificationManager.notify(NOTIFYCATION_ID++, notifyBuilder.build());
        }

    }

}
