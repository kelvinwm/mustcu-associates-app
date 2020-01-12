package com.beyondthehorizon.associates.notifications;

public class AllNotifiChatModel {
    private String senderName;
    private String theMessage;

    public AllNotifiChatModel(String senderName, String theMessage) {
        this.senderName = senderName;
        this.theMessage = theMessage;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getTheMessage() {
        return theMessage;
    }
}
