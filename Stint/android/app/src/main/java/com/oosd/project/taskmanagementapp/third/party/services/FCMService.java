package com.oosd.project.taskmanagementapp.third.party.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.oosd.project.taskmanagementapp.Activities.LoginActivity;
import com.oosd.project.taskmanagementapp.R;

import androidx.core.app.NotificationCompat;

public class FCMService extends FirebaseMessagingService {


    // Method triggered when FCM updates the FCM device token
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.e("newToken: ", token);
        // Storing the token in shared preferences
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fcm_token", token).apply();
    }

    // Method triggered when FCM pushes a notification
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("msg", "onMessageReceived: " + remoteMessage.getNotification().getBody());
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Developing notification frame which appears in the notification tray when the notification is received.
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // If SDK level is greater than 26, and handle those OS versions in a different way.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        // Finally notifying the user with push notification
        manager.notify(0, builder.build());
    }

    //Method to return FM token from shared preferences
    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fcm_token", "empty");
    }
}