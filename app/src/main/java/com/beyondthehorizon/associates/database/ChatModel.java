package com.beyondthehorizon.associates.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "chats_table")
public class ChatModel {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String message_key;
    public String senderName;
    public String message;
    public String comments;
    public String phoneNumber;
    public String senderUID;
    public String timestamp;
    public String receiverUID;
    public String type;

    @Ignore
    public ChatModel() {
    }

    public ChatModel(String message_key, String senderName, String message, String comments,
                     String phoneNumber, String senderUID, String timestamp, String receiverUID, String type) {
        this.message_key = message_key;
        this.senderName = senderName;
        this.message = message;
        this.comments = comments;
        this.phoneNumber = phoneNumber;
        this.senderUID = senderUID;
        this.timestamp = timestamp;
        this.receiverUID = receiverUID;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getMessage_key() {
        return message_key;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }

    public String getComments() {
        return comments;
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

    public String getType() {
        return type;
    }
}
