package com.beyondthehorizon.associates.database;

import android.net.Uri;

public class SendingImagesModel {
    Uri imageUri;
    String txtMessage;

    public SendingImagesModel(Uri imageUri, String txtMessage) {
        this.imageUri = imageUri;
        this.txtMessage = txtMessage;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getTxtMessage() {
        return txtMessage;
    }

    public void setTxtMessage(String txtMessage) {
        this.txtMessage = txtMessage;
    }
}
