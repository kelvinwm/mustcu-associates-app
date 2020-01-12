package com.beyondthehorizon.associates.notifications;

public class MessageChat {
    private CharSequence text;
    private long timestamp;
    private CharSequence sender;

    public MessageChat(CharSequence text, CharSequence sender) {
        this.text = text;
        this.sender = sender;
        timestamp = System.currentTimeMillis();
    }

    public CharSequence getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public CharSequence getSender() {
        return sender;
    }

}