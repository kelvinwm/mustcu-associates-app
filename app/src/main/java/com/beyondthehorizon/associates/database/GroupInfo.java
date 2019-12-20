package com.beyondthehorizon.associates.database;

public class GroupInfo {
    String profileImage;
    String numberOfMembers;

    public GroupInfo(String imageUrl, String numberOfMembers) {
        this.profileImage = imageUrl;
        this.numberOfMembers = numberOfMembers;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getNumberOfMembers() {
        return numberOfMembers;
    }
}
