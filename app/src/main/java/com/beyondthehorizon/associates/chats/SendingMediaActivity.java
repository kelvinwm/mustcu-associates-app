package com.beyondthehorizon.associates.chats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.adapters.SendingImagesAdapter;
import com.beyondthehorizon.associates.database.SendingImagesModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.util.ArrayList;

import okhttp3.MediaType;

public class SendingMediaActivity extends AppCompatActivity implements SendingImagesAdapter.SendMyTxtImage {

    VideoView videoView;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    private ArrayList<MediaFile> select = new ArrayList<>();
    private SendingImagesAdapter imagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_media);

        recyclerView = findViewById(R.id.mediaRecy);
        fab = findViewById(R.id.sendMedia);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("TAG2", "onClick: " + imagesAdapter.sendingImagesModelArrayList.get(0).getTxtMessage());
                sendTextImage(imagesAdapter.sendingImagesModelArrayList);
            }
        });
//        videoView = findViewById(R.id.videoView);
//        videoView.setVideoURI(Uri.parse("/storage/emulated/0/WhatsApp/MediaFile/WhatsApp Video/VID-20191226-WA0001.mp4"));
//        final MediaController mediaController = new MediaController(this);
//        videoView.setMediaController(mediaController);
//        mediaController.setAnchorView(videoView);
//        final int seekTimeMs =1000; // 1
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                videoView.seekTo(seekTimeMs);
//            }
//        });

//                    RecyclerView rvTest = dialog2.findViewById(R.id.rvTest);
        Intent intent = getIntent();
        select = (ArrayList<MediaFile>) intent.getSerializableExtra("ikosawa");

        ArrayList<SendingImagesModel> allMedia = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        for (MediaFile uri : select) {
//                Log.d(TAG, "onActivityResult: " + uri.mediaType + "  " + Uri.fromFile(new File(uri.path)));
            Log.d("TAG", "onActivityResult: " + uri.getSize());

            String mediaType = "IMG";
            if (uri.getMediaType() == MediaFile.TYPE_VIDEO) {
                mediaType = "Video";
            }
            if (uri.getMediaType() == MediaFile.TYPE_FILE) {
                mediaType = "Doc";
            }
//            //FILE SHOULD BE LESS THAN 20MB
//            if (uri.getSize() < 20000000) {
            allMedia.add(new SendingImagesModel(uri.getPath(), mediaType, String.valueOf(uri.getMediaType()), uri));
            imagesAdapter = new SendingImagesAdapter(SendingMediaActivity.this, allMedia, this);
            recyclerView.setAdapter(imagesAdapter);
//            }
//                imagesAdapter = new SendingImagesAdapter(ChatActivity.this, allMedia, this);
//                mediaRecyclerView.setAdapter(imagesAdapter);
        }
    }

    @Override
    public void sendTextImage(ArrayList<SendingImagesModel> arrayList) {
        ChatActivity chatActivity = new ChatActivity();
        chatActivity.sendTextImage(arrayList);
        finish();
    }
}
