package com.beyondthehorizon.associates.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "latest_chats")
public class RecentChatModel {
    @PrimaryKey
    @NonNull
    private String senderUID; //This on my side will be receiverUID
    private String username;
    private String message;
    private String time;
    private String type;
    private String imageUrl;

    public RecentChatModel(@NonNull String senderUID, String username, String message, String time,
                           String type, String imageUrl) {
        this.senderUID = senderUID;
        this.username = username;
        this.message = message;
        this.time = time;
        this.type = type;
        this.imageUrl = imageUrl;
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

    public String getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
