package com.beyondthehorizon.testfirebasefunctionstest.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "latest_chats")
public class RecentChatModel {
    @PrimaryKey
    @NonNull
    String senderUID; //This on my side will be receiverUID
    String username;
    String message;
    String time;

    public RecentChatModel(@NonNull String senderUID, String username, String message, String time) {
        this.senderUID = senderUID;
        this.username = username;
        this.message = message;
        this.time = time;
    }

    @NonNull
    public String getSenderUID() {
        return senderUID;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}
