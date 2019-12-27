package com.beyondthehorizon.associates.database;

import android.net.Uri;

import com.jaiselrahman.filepicker.model.MediaFile;

public class SendingImagesModel {
    String imageUri;
    String txtMessage;
    String mediaType;
    MediaFile mediaFile;

    public SendingImagesModel(String imageUri, String txtMessage, String mediaType, MediaFile mediaFile) {
        this.imageUri = imageUri;
        this.txtMessage = txtMessage;
        this.mediaType = mediaType;
        this.mediaFile = mediaFile;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
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
