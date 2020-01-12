package com.beyondthehorizon.associates;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class AppController extends Application implements LifecycleObserver {
    public boolean appStatus;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public static final String GROUP_1_ID = "group1";
    public static final String GROUP_2_ID = "group2";
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";
    public static final String CHANNEL_4_ID = "channel4";

    ///////////////////////////////////////////////
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Log.d("AppController", "Foreground");
        isAppInBackground(false);
        appStatus = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.d("AppController", "Background");
        isAppInBackground(true);
        appStatus = true;
        if (!currentUser.getUid().isEmpty()) {
            DatabaseReference userStatus =
                    FirebaseDatabase.getInstance().getReference("Users/UserProfile/" + currentUser.getUid() + "/onlineStatus");
            userStatus.onDisconnect().setValue(ServerValue.TIMESTAMP);
            userStatus.setValue("Paused");
        }
    }
///////////////////////////////////////////////


    // Adding some callbacks for test and log
    public interface ValueChangeListener {
        void onChanged(Boolean value);
    }

    private ValueChangeListener visibilityChangeListener;

    public void setOnVisibilityChangeListener(ValueChangeListener listener) {
        this.visibilityChangeListener = listener;
    }

    private void isAppInBackground(Boolean isBackground) {
        if (null != visibilityChangeListener) {
            visibilityChangeListener.onChanged(isBackground);
        }
    }

    private static AppController mInstance;

    public static AppController getInstance() {
        return mInstance;
    }

/////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mInstance = this;
        // addObserver
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        createNotificationChannels();
    }


    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelGroup group1 = new NotificationChannelGroup(
                    GROUP_1_ID,
                    "Group 1"
            );
            NotificationChannelGroup group2 = new NotificationChannelGroup(
                    GROUP_2_ID,
                    "Group 2"
            );

            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");
            channel1.setGroup(GROUP_1_ID);

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Channel 2");
            channel2.setGroup(GROUP_1_ID);

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "Channel 3",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel3.setDescription("This is Channel 3");
            channel3.setGroup(GROUP_2_ID);

            NotificationChannel channel4 = new NotificationChannel(
                    CHANNEL_4_ID,
                    "Channel 4",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel4.setDescription("This is Channel 4");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannelGroup(group1);
            manager.createNotificationChannelGroup(group2);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
            manager.createNotificationChannel(channel4);
        }
    }
}