package com.beyondthehorizon.associates.viewdetails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.beyondthehorizon.associates.R;

import java.io.File;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView viewImage;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Intent intent = getIntent();
        String imgUri = intent.getStringExtra("Uri");
        String mediaType = intent.getStringExtra("mediaType");
        viewImage = findViewById(R.id.viewImage);
        videoView = findViewById(R.id.videoView);

        if (mediaType.contains("video")) {
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(imgUri));
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);
//            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    videoView.seekTo(0);
//                }
//            });
            videoView.start();
        } else {
            viewImage.setVisibility(View.VISIBLE);
            viewImage.setImageURI(Uri.fromFile(new File(imgUri)));
        }
    }
}
