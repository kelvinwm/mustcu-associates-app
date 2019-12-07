package com.beyondthehorizon.associates.database;

public class GroupInfo {
    String userToken;
    String userRole;
    String userPhone;

    public GroupInfo(String userToken, String userRole, String userPhone) {
        this.userToken = userToken;
        this.userRole = userRole;
        this.userPhone = userPhone;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUserPhone() {
        return userPhone;
    }
}