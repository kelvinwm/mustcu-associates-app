package com.beyondthehorizon.associates.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.util.ArrayList;

import okhttp3.MediaType;

import static com.beyondthehorizon.associates.MainActivity.CHAT_PREFS;
import static com.beyondthehorizon.associates.MainActivity.ChatTypeFromChatsFragment;
import static com.beyondthehorizon.associates.MainActivity.FriendUID;
import static com.beyondthehorizon.associates.MainActivity.MyFriendName;
import static com.beyondthehorizon.associates.util.Constants.NothingToSend;

public class SendingMediaActivity extends AppCompatActivity implements SendingImagesAdapter.SendMyTxtImage {

    VideoView videoView;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    private ArrayList<MediaFile> select = new ArrayList<>();
    private SendingImagesAdapter imagesAdapter;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String profileUrl;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_media);

        recyclerView = findViewById(R.id.mediaRecy);
        fab = findViewById(R.id.sendMedia);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        SharedPreferences chatPref = getApplicationContext().getSharedPreferences(CHAT_PREFS, 0);
        final String friend_Uid = chatPref.getString(FriendUID, "");
        final String chatType = chatPref.getString(ChatTypeFromChatsFragment, "");
        final String myFriend_Name = chatPref.getString(MyFriendName, "");

        myRef.child("Users").child("UserProfile").child(currentUser.getUid())
                .child("imageUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists())) {
                    profileUrl = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("TAG2", "onClick: " + imagesAdapter.sendingImagesModelArrayList.get(0).getTxtMessage());
                for (SendingImagesModel model : imagesAdapter.sendingImagesModelArrayList) {
                    if (model.getMediaType().contains("0")) {
                        sendTextImage(imagesAdapter.sendingImagesModelArrayList,
                                NothingToSend, NothingToSend, NothingToSend,
                                model.getImageUri(), profileUrl, friend_Uid, chatType, myFriend_Name);

                    } else if (model.getMediaType().contains("1")) {
                        sendTextImage(imagesAdapter.sendingImagesModelArrayList,
                                model.getImageUri(), NothingToSend, NothingToSend,
                                NothingToSend, profileUrl, friend_Uid, chatType, myFriend_Name);

                    } else if (model.getMediaType().contains("2")) {
                        sendTextImage(imagesAdapter.sendingImagesModelArrayList,
                                NothingToSend, NothingToSend, model.getImageUri(),
                                NothingToSend, profileUrl, friend_Uid, chatType, myFriend_Name);

                    } else if (model.getMediaType().contains("3")) {

                        sendTextImage(imagesAdapter.sendingImagesModelArrayList,
                                NothingToSend, model.getImageUri(), NothingToSend,
                                NothingToSend, profileUrl, friend_Uid, chatType, myFriend_Name);
                    }
                }
//                sendTextImage(imagesAdapter.sendingImagesModelArrayList, imageUrl, videoUrl, audioUrl, fileUrl);
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
            if (uri.getMediaType() == MediaFile.TYPE_AUDIO) {
                mediaType = "Audio";
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
    public void sendTextImage(ArrayList<SendingImagesModel> arrayList, String imageUrl, String videoUrl,
                              String audioUrl, String fileUrl, String profileUrl, String friend_Uid,
                              String chatType, String myFriend_Name) {

        ChatActivity chatActivity = new ChatActivity();
        startActivity(new Intent(SendingMediaActivity.this, ChatActivity.class));
        chatActivity.sendTextImage(arrayList, imageUrl, videoUrl, audioUrl, fileUrl, profileUrl,
                friend_Uid, chatType, myFriend_Name);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (imagesAdapter.mPlayer != null) {
            imagesAdapter.mPlayer.stop();
            imagesAdapter.mPlayer.release();
        }
    }
}
