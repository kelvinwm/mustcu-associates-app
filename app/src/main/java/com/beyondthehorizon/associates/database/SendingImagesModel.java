package com.beyondthehorizon.associates.database;

import android.net.Uri;

public class SendingImagesModel {
    String imageUri;
    String txtMessage;
    String mediaType;

    public SendingImagesModel(String imageUri, String txtMessage, String mediaType) {
        this.imageUri = imageUri;
        this.txtMessage = txtMessage;
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getTxtMessage() {
        return txtMessage;
    }

    public void setTxtMessage(String txtMessage) {
        this.txtMessage = txtMessage;
    }
}
