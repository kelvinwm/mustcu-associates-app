package com.beyondthehorizon.testfirebasefunctionstest;

public class UserProfile {
    String userToken;
    String userUid;
    String userName;
    String phoneNumber;

    public UserProfile() {
    }

    public UserProfile(String userToken, String userUid, String userName, String phoneNumber) {
        this.userToken = userToken;
        this.userUid = userUid;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
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
}
