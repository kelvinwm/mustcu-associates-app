package com.beyondthehorizon.associates.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "comments_table")
public class CommentsModel {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String message_key;
    public String senderName;
    public String message;
    public String phoneNumber;
    public String senderUID;
    public String timestamp;
    public String docName;
    public String imageUrl;
    public String videoUrl;
    public String audioUrl;
    public String fileUrl;
    public String deliveryState;
    public String type;

    @Ignore
    public CommentsModel() {
    }

    public CommentsModel(String message_key, String senderName, String message, String phoneNumber,
                         String senderUID, String timestamp, String docName, String imageUrl,
                         String videoUrl, String audioUrl, String fileUrl, String deliveryState, String type) {
        this.message_key = message_key;
        this.senderName = senderName;
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.senderUID = senderUID;
        this.timestamp = timestamp;
        this.docName = docName;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
        this.fileUrl = fileUrl;
        this.deliveryState = deliveryState;
        this.type = type;
    }

    public String getDocName() {
        return docName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDeliveryState() {
        return deliveryState;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSenderUID() {
        return senderUID;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }
}
