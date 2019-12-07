package com.beyondthehorizon.associates.database;

public class UserProfile {
    String userToken;
    String userUid;
    String userName;
    String phoneNumber;
    String imageUrl;

    public UserProfile() {
    }

    public UserProfile(String userToken, String userUid, String userName, String phoneNumber, String imageUrl) {
        this.userToken = userToken;
        this.userUid = userUid;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getUserUid() {
        return userUid;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
