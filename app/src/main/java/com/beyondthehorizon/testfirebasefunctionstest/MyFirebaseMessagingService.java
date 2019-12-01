package com.beyondthehorizon.testfirebasefunctionstest;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.beyondthehorizon.testfirebasefunctionstest.Repositories.ChatsRepository;
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
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("title", dataTitle);
        intent.putExtra("message", dataMessage);
        intent.putExtra("myFriend", dataTitle);
        intent.putExtra("friendUID", senderUID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(dataTitle)
                .setContentText(dataMessage)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
