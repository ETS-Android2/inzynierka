package com.example.filip_derbin_praca_inzynierska;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        notify(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    public void notify(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notification_channel")
             .setSmallIcon(R.drawable.ic_launcher)
             .setContentTitle(title)
             .setContentText(message)
             .setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(123, builder.build());
    }
}
