package com.beyondthehorizon.testfirebasefunctionstest;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "chats_table")
public class ChatModel {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String senderName;
    public String message;
    public String phoneNumber;
    public String senderUID;
    public String timestamp;
    public String receiverUID;

    @Ignore
    public ChatModel() {
    }

    public ChatModel(String senderName, String message, String phoneNumber, String senderUID,
                     String timestamp, String receiverUID) {
        this.senderName = senderName;
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.senderUID = senderUID;
        this.timestamp = timestamp;
        this.receiverUID = receiverUID;
    }

    public int getId() {
        return id;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getReceiverUID() {
        return receiverUID;
    }
}
