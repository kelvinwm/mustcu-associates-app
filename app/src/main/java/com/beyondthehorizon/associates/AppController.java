package com.beyondthehorizon.associates;

import android.app.Application;
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
    }
}