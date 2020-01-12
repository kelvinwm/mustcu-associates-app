package com.beyondthehorizon.associates.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.RemoteInput;

import com.beyondthehorizon.associates.main.ChatsFragment;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        if (remoteInput != null) {
            CharSequence replyText = remoteInput.getCharSequence("key_text_reply");
            MessageChat answer = new MessageChat(replyText, null);
            ChatsFragment.MESSAGES.add(answer);

//            ChatsFragment.sendChannel1Notification(context);
        }
    }
}
