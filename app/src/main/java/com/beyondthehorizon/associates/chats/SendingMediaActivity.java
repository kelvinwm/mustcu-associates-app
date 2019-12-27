package com.beyondthehorizon.associates.chats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.adapters.SendingImagesAdapter;
import com.beyondthehorizon.associates.database.SendingImagesModel;
import com.dmcbig.mediapicker.entity.Media;

import java.util.ArrayList;

public class SendingMediaActivity extends AppCompatActivity implements SendingImagesAdapter.SendMyTxtImage {

    VideoView videoView;
    RecyclerView recyclerView;
    private ArrayList<Media> select = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_media);

        recyclerView = findViewById(R.id.mediaRecy);
//        videoView = findViewById(R.id.videoView);
//        videoView.setVideoURI(Uri.parse("/storage/emulated/0/WhatsApp/Media/WhatsApp Video/VID-20191226-WA0001.mp4"));
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
//            TextView totalImgs = dialog2.findViewById(R.id.totalImgs);
//        String numOfImges = select.size() + " item(s)";
//        totalImgs.setText(numOfImges);
        Intent intent = getIntent();
        select = (ArrayList<Media>) intent.getSerializableExtra("ikosawa");
        ArrayList<SendingImagesModel> allMedia = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        for (Media uri : select) {
//                Log.d(TAG, "onActivityResult: " + uri.mediaType + "  " + Uri.fromFile(new File(uri.path)));
            Log.d("TAG", "onActivityResult: " + uri.path);

            allMedia.add(new SendingImagesModel(uri.path, "IMG", String.valueOf(uri.mediaType)));
            SendingImagesAdapter  imagesAdapter = new SendingImagesAdapter(SendingMediaActivity.this, allMedia, this);
            recyclerView.setAdapter(imagesAdapter);
//                imagesAdapter = new SendingImagesAdapter(ChatActivity.this, allMedia, this);
//                mediaRecyclerView.setAdapter(imagesAdapter);
        }
    }

    @Override
    public void sendTextImage(ArrayList<SendingImagesModel> arrayList) {

    }
}
