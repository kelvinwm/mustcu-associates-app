package com.beyondthehorizon.associates.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.adapters.CommentsAdapter;
import com.beyondthehorizon.associates.database.ChatModel;
import com.beyondthehorizon.associates.database.CommentsModel;
import com.beyondthehorizon.associates.database.RecentChatModel;
import com.beyondthehorizon.associates.users.UserProfileActivity;
import com.beyondthehorizon.associates.viewmodels.ChatsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static com.beyondthehorizon.associates.chats.ChatActivity.MY_SHARED_PREF;
import static com.beyondthehorizon.associates.util.Constants.Delivered;
import static com.beyondthehorizon.associates.util.Constants.Failed;
import static com.beyondthehorizon.associates.util.Constants.NothingToSend;
import static com.beyondthehorizon.associates.util.Constants.Sending;
import static com.beyondthehorizon.associates.util.Constants.Sent;

public class CommentsChatActivity extends AppCompatActivity {
    private static final int GALLERY_PICK = 300;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    //    private Button sendData, sendGroup;
    public ArrayList<MediaFile> select = new ArrayList<>();
    private ImageButton sendData;
    private EmojiconEditText sampleMessage;
    public static final String TAG = "COMMENTSACTIVITY";
    private ChatsViewModel chatsViewModel;
    private CommentsAdapter commentsAdapter;
    private ImageView emojiImageView, attachButton;
    private View rootView;
    private EmojIconActions emojIcon;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SharedPreferences pref;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_chat);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Comments");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        final Intent intent = getIntent();
        pref = getApplicationContext().getSharedPreferences(MY_SHARED_PREF, 0);

        attachButton = findViewById(R.id.attachButton);
        recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setHasFixedSize(true);
        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
        commentsAdapter = new CommentsAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(commentsAdapter);
        sampleMessage = findViewById(R.id.sampleMessage);
        sendData = findViewById(R.id.sendData);

        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        emojiImageView = (ImageView) findViewById(R.id.emojiButton);
        rootView = findViewById(R.id.root_view);
        emojIcon = new EmojIconActions(this, rootView, sampleMessage, emojiImageView);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard closed");
            }
        });

        myRef.child("Users").child("UserProfile").child(currentUser.getUid())
                .child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.exists())) {
                    startActivity(new Intent(CommentsChatActivity.this, UserProfileActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Write a message to the database
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sampleMessage.getText().toString().trim();
                if (message.isEmpty()) {
                    return;
                }
                Date today = new Date();
                SimpleDateFormat format = new SimpleDateFormat("EEE MMM d ''yy  HH:mm a", Locale.getDefault());
                String dateToStr = format.format(today);

                String msg_key = myRef.child("Users").child("UserComments").push().getKey();


                //Save locally
                chatsViewModel.insertComment(new CommentsModel(
                        intent.getStringExtra("MainQuestionKey"),
                        msg_key,
                        currentUser.getDisplayName(),
                        message,
                        currentUser.getPhoneNumber(),
                        currentUser.getUid(),
                        dateToStr,
                        NothingToSend,
                        NothingToSend,
                        NothingToSend,
                        NothingToSend,
                        NothingToSend,
                        Sending,
                        "Comment"));

                sendMessage(message, NothingToSend, NothingToSend,
                        NothingToSend, NothingToSend, dateToStr, msg_key, NothingToSend);

                sampleMessage.setText("");
            }
        });
        getComments(intent.getStringExtra("MainQuestionKey"));
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMediaStaff();
            }
        });
    }

    private void getComments(String message_key) {
        chatsViewModel.getQuestionComments(message_key).observe(this, new Observer<List<CommentsModel>>() {
            @Override
            public void onChanged(List<CommentsModel> commentsModels) {
                commentsAdapter.setProMemberArrayList((ArrayList<CommentsModel>) commentsModels);
                recyclerView.smoothScrollToPosition(commentsModels.size() - 1);
            }
        });
    }

    private void sendMessage(String message, String imageUrl, String videoUrl, String audioUrl, String fileUrl,
                             String dateToStr, final String msg_key, String docName) {
        Intent intent = getIntent();

        CommentsModel commentsModel = new CommentsModel(
                intent.getStringExtra("MainQuestionKey"),
                msg_key,
                currentUser.getDisplayName(),
                message,
                currentUser.getPhoneNumber(),
                currentUser.getUid(),
                dateToStr,
                docName,
                imageUrl,
                videoUrl,
                audioUrl,
                fileUrl,
                Sent,
                "Comment");

        /**SEND TO FIRE BASE ROOM CHAT COMMENTS*/
        myRef.child("Rooms").child(intent.getStringExtra("RoomId"))
                .child(pref.getString("friend_name", null))
                .child("UserComments").child(intent.getStringExtra("MainQuestionKey"))
                .child(msg_key).setValue(commentsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: has been sent");
                    chatsViewModel.updateCommentDeliveryStatus(msg_key, Delivered);
                } else {
                    chatsViewModel.updateCommentDeliveryStatus(msg_key, Failed);
                }
            }
        });
    }


    private void getMediaStaff() {
        //Check Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(CommentsChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(CommentsChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(CommentsChatActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                ActivityCompat.requestPermissions(CommentsChatActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA}, 100);
                return;
            }
        }
        // custom dialog
        final Dialog dialog = new Dialog(CommentsChatActivity.this);
        dialog.setContentView(R.layout.media_dialog);

        RelativeLayout pickCamera = dialog.findViewById(R.id.pickCamera);
        RelativeLayout pickImage = dialog.findViewById(R.id.pickImage);
        RelativeLayout pickVideo = dialog.findViewById(R.id.pickVideo);
        RelativeLayout pickAudio = dialog.findViewById(R.id.pickAudio);
        RelativeLayout pickFile = dialog.findViewById(R.id.pickFile);

        pickCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
//                Intent intent = new Intent(CommentsChatActivity.this, TakePhotoActivity.class); //Take a photo with a camera
//                startActivityForResult(intent, GALLARY_PICK);
                dialog.dismiss();
            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
                Intent intent = new Intent(CommentsChatActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(true)
                        .setShowAudios(false)
                        .setShowFiles(false)
                        .setShowVideos(false)
                        .enableImageCapture(true)
                        .setMaxSelection(5)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityForResult(intent, GALLERY_PICK);
                dialog.dismiss();
            }
        });
        pickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
                Intent intent = new Intent(CommentsChatActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .setShowAudios(false)
                        .setShowFiles(false)
                        .setShowVideos(true)
                        .enableVideoCapture(true)
                        .setMaxSelection(3)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityForResult(intent, GALLERY_PICK);
                dialog.dismiss();
            }
        });
        pickAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
//                Intent intent_upload = new Intent();
//                intent_upload.setType("audio/*");
//                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent_upload, 1);
                Intent intent = new Intent(CommentsChatActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .setShowAudios(true)
                        .setShowFiles(false)
                        .setShowVideos(false)
                        .setIgnoreHiddenFile(false)
                        .setMaxSelection(3)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityForResult(intent, GALLERY_PICK);
                dialog.dismiss();
            }
        });
        pickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
                Intent intent = new Intent(CommentsChatActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .setShowAudios(false)
                        .setShowFiles(true)
                        .setShowVideos(false)
                        .setSuffixes("txt", "pdf", "html", "rtf", "csv", "xml",
                                "zip", "tar", "gz", "rar", "7z", "torrent", "doc", "docx", "odt", "ott",
                                "ppt", "pptx", "pps", "xls", "xlsx", "ods", "ots")
                        .setMaxSelection(5)
                        .setSkipZeroSizeFiles(true)
                        .build());
                startActivityForResult(intent, GALLERY_PICK);
                dialog.dismiss();
            }
        });

        //CHOOSE DIALOG
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.y = 150;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialog.getWindow().setAttributes(wlp);
//            dialog.getWindow().setLayout((6 * width) / 7, (3 * height) / 5);

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            select = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            assert select != null;
            if (select.size() == 0) {
                return;
            }
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM d ''yy  HH:mm a", Locale.getDefault());
            final String dateToStr = format.format(today);

            for (final MediaFile mediaFile : select) {
                final String msg_key = myRef.child("Users").child("UserChats").push().getKey();

                if (String.valueOf(mediaFile.getMediaType()).contains("0")) {
                    sendMediaContent(mediaFile.getName(), "Doc", NothingToSend,
                            NothingToSend, NothingToSend, mediaFile.getPath(), msg_key, dateToStr);

                    //SEND TO FIRE BASE
                    final StorageReference filePath = storageReference.child("Chat Documents")
                            .child(msg_key + mediaFile.getUri().getLastPathSegment());
                    filePath.putFile(Uri.fromFile(new File(mediaFile.getPath())))
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "onSuccess: " + uri.toString());
                                            sendMessage("Doc", NothingToSend,
                                                    NothingToSend, NothingToSend
                                                    , uri.toString(), dateToStr, msg_key,
                                                    mediaFile.getName());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            chatsViewModel.updateDeliveryStatus(msg_key, Failed);
                        }
                    });

                } else if (String.valueOf(mediaFile.getMediaType()).contains("1")) {
                    sendMediaContent(mediaFile.getName(), "IMG", mediaFile.getPath(),
                            NothingToSend, NothingToSend, NothingToSend, msg_key, dateToStr);

                    //SEND TO FIRE BASE
                    final StorageReference filePath = storageReference.child("Chat Images")
                            .child(msg_key + mediaFile.getUri().getLastPathSegment());
                    filePath.putFile(Uri.fromFile(new File(mediaFile.getPath())))
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "onSuccess: " + uri.toString());
                                            sendMessage("IMG", uri.toString(), NothingToSend,
                                                    NothingToSend, NothingToSend, dateToStr, msg_key,
                                                    mediaFile.getName());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            chatsViewModel.updateDeliveryStatus(msg_key, Failed);
                        }
                    });

                } else if (String.valueOf(mediaFile.getMediaType()).contains("2")) {
                    sendMediaContent(mediaFile.getName(), "Audio", NothingToSend,
                            NothingToSend, mediaFile.getPath(), NothingToSend, msg_key, dateToStr);

                    //SEND TO FIRE BASE
                    final StorageReference filePath = storageReference.child("Chat Audios")
                            .child(msg_key + mediaFile.getUri().getLastPathSegment());
                    filePath.putFile(Uri.fromFile(new File(mediaFile.getPath())))
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "onSuccess: " + uri.toString());
                                            sendMessage("Audio", NothingToSend,
                                                    NothingToSend, uri.toString(),
                                                    NothingToSend, dateToStr, msg_key,
                                                    mediaFile.getName());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            chatsViewModel.updateDeliveryStatus(msg_key, Failed);
                        }
                    });

                } else if (String.valueOf(mediaFile.getMediaType()).contains("3")) {
                    sendMediaContent(mediaFile.getName(), "Video", NothingToSend,
                            mediaFile.getPath(), NothingToSend, NothingToSend, msg_key, dateToStr);

                    //SEND TO FIRE BASE
                    final StorageReference filePath = storageReference.child("Chat Videos")
                            .child(msg_key + mediaFile.getUri().getLastPathSegment());
                    filePath.putFile(Uri.fromFile(new File(mediaFile.getPath())))
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "onSuccess: " + uri.toString());
                                            sendMessage("Video", NothingToSend,
                                                    uri.toString(), NothingToSend,
                                                    NothingToSend, dateToStr, msg_key,
                                                    mediaFile.getName());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                            chatsViewModel.updateDeliveryStatus(msg_key, Failed);
                        }
                    });
                }
            }
        }
    }


    public void sendMediaContent(final String mediaName, final String message, String imageUrl,
                                 String videoUrl, String audioUrl, String fileUrl, String msg_key,
                                 String dateToStr) {
        Intent intent = getIntent();
        //GET MEDIATYPE FROM MEDIAFILE

        //Save locally
        chatsViewModel.insertComment(new CommentsModel(
                intent.getStringExtra("MainQuestionKey"),
                msg_key,
                currentUser.getDisplayName(),
                message,
                currentUser.getPhoneNumber(),
                currentUser.getUid(),
                dateToStr,
                mediaName,
                imageUrl,
                videoUrl,
                audioUrl,
                fileUrl,
                Sending,
                "Comment"));

    }
}