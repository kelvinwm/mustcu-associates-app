package com.beyondthehorizon.associates.viewdetails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.beyondthehorizon.associates.R;

import java.io.File;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView viewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Intent intent = getIntent();
        String imgUri = intent.getStringExtra("imageUri");
        viewImage = findViewById(R.id.viewImage);
        viewImage.setImageURI(Uri.fromFile(new File(imgUri)));
    }
}
