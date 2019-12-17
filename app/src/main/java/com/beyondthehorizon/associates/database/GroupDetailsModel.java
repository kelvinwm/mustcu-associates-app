package com.beyondthehorizon.associates.database;

public class GroupDetailsModel {
    String userName;
    String userRole;
    String userPhone;
    String imageUrl;
    String userTagline;

    public GroupDetailsModel(String userName, String userRole, String userPhone, String imageUrl, String userTagline) {
        this.userName = userName;
        this.userRole = userRole;
        this.userPhone = userPhone;
        this.imageUrl = imageUrl;
        this.userTagline = userTagline;
    }

    public String getUserName() {
        return userName;
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

    public String getUserTagline() {
        return userTagline;
    }
}
