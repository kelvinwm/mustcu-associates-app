package com.beyondthehorizon.associates.database;

public class GroupUserInfo {
    String userToken;
    String userRole;
    String userPhone;
    String imageUrl;

    public GroupUserInfo(String userToken, String userRole, String userPhone, String imageUrl) {
        this.userToken = userToken;
        this.userRole = userRole;
        this.userPhone = userPhone;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }
}
