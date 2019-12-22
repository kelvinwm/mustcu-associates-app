package com.beyondthehorizon.associates.database;

import android.net.Uri;

public class SendingImagesModel {
    String imageUri;
    String txtMessage;

    public SendingImagesModel(String imageUri, String txtMessage) {
        this.imageUri = imageUri;
        this.txtMessage = txtMessage;
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
