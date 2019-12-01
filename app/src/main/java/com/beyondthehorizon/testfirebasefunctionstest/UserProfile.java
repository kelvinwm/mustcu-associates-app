package com.beyondthehorizon.testfirebasefunctionstest;

public class UserProfile {
    String userToken;
    String userUid;
    String userName;

    public UserProfile() {
    }

    public UserProfile(String userToken, String userUid, String userName) {
        this.userToken = userToken;
        this.userUid = userUid;
        this.userName = userName;
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
}
