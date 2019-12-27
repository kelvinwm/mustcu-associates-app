package com.beyondthehorizon.associates.database;

public class GroupUserInfo {
    String userToken;
    String userRole;
    String userPhone;
    String profileImage;

    public GroupUserInfo(String userToken, String userRole, String userPhone, String profileImage) {
        this.userToken = userToken;
        this.userRole = userRole;
        this.userPhone = userPhone;
        this.profileImage = profileImage;
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

    public String getProfileImageUrl() {
        return profileImage;
    }
}
