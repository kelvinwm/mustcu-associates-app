package com.beyondthehorizon.associates.database;

public class GroupInfo {
    String imageUrl;
    String numberOfMembers;

    public GroupInfo(String imageUrl, String numberOfMembers) {
        this.imageUrl = imageUrl;
        this.numberOfMembers = numberOfMembers;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getNumberOfMembers() {
        return numberOfMembers;
    }
}
