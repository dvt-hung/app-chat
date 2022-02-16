package com.example.appchat.model;

import java.io.Serializable;

public class Message implements Serializable {
    private String senderID;
    private String receiverID;
    private String message;
    private long time;
    private boolean seen;

    public Message() {
    }



    public Message(String senderID, String receiverID, String message, long time, boolean seen) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.time = time;
        this.seen = seen;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
