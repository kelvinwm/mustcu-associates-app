package com.beyondthehorizon.testfirebasefunctionstest;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.beyondthehorizon.testfirebasefunctionstest.Repositories.ChatsRepository;
import com.beyondthehorizon.testfirebasefunctionstest.database.RecentChatModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = "MESSAGING";
    private ChatsRepository chatsRepository;
//    private boolean appStatus;

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        /* There are two types of messages data messages and notification messages.
        Data messages are handled here in onMessageReceived whether the app is in the foreground or background.
        Data messages are the type traditionally used with GCM.
        Notification messages are only received here in onMessageReceived when the app is in the foreground.
        When the app is in the background an automatically generated notification is displayed. */

        chatsRepository = new ChatsRepository(getApplication());
        String dataTitle, dataMessage, phoneNumber, senderUID,
                receiverUID, timestamp;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("message"));
            dataTitle = remoteMessage.getData().get("sender");
            dataMessage = remoteMessage.getData().get("message");
            phoneNumber = remoteMessage.getData().get("phoneNumber");
            senderUID = remoteMessage.getData().get("senderUID");
            timestamp = remoteMessage.getData().get("timestamp");
            receiverUID = remoteMessage.getData().get("receiverUID");

            final String finalDataTitle = dataTitle;
            final String finalDataMessage = dataMessage;

            chatsRepository.insertChat(new ChatModel(
                    dataTitle,
                    dataMessage,
                    phoneNumber,
                    senderUID,
                    timestamp,
                    receiverUID
            ));

            chatsRepository.insertLatestChat(new RecentChatModel(
                    senderUID,
                    dataTitle,
                    dataMessage,
                    timestamp
            ));
            Log.d(TAG, "onMessageReceived: " + AppController.getInstance().appStatus);
            if (AppController.getInstance().appStatus) {
                // Also if you intend on generating your own notifications as a result of a received FCM
                // message, here is where that should be initiated. See sendNotification method below.
                sendNotification(finalDataTitle, finalDataMessage, senderUID);
            }
        }
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            notificationTitle = remoteMessage.getNotification().getTitle();
//            notificationBody = remoteMessage.getNotification().getBody();
//        }
    }

    /**
     * //     * Create and show a simple notification containing the received FCM message.
     * //
     */
    private void sendNotification(String dataTitle, String dataMessage, String senderUID) {

        Intent intentAction = new Intent(this, ActionReceiver.class);
        intentAction.putExtra("action", "action1");
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 1,
                intentAction, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("title", dataTitle);
        intent.putExtra("message", dataMessage);
        intent.putExtra("myFriend", dataTitle);
        intent.putExtra("friendUID", senderUID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // This is the Notification Channel ID. More about this in the next section
        final String NOTIFICATION_CHANNEL_ID = "channel_id";
        //User visible Channel Name
        final String CHANNEL_NAME = "Notification Channel";

        //Notification channel should only be created for devices running Android 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Importance applicable to all the notifications in this Channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
            //Boolean value to set if lights are enabled for Notifications from this Channel
            notificationChannel.enableLights(true);
            //Boolean value to set if vibration are enabled for Notifications from this Channel
            notificationChannel.enableVibration(true);
            //Sets the color of Notification Light
            notificationChannel.setLightColor(Color.GREEN);
            //Set the vibration pattern for notifications. Pattern is in milliseconds with the format {delay,play,sleep,play,sleep...}
            notificationChannel.setVibrationPattern(new long[]{
                    500,
                    500,
                    500,
                    500,
                    500
            });
            //Sets whether notifications from these Channel should be visible on Lockscreen or not
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(notificationChannel);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_account)
                .setContentTitle(dataTitle)
                .setContentText(dataMessage)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_account, "Later", pendingIntent2)
                .addAction(R.drawable.ic_send, "Reply", pendingIntent2);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(101, builder.build());
    }
}
