package com.beyondthehorizon.associates.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.adapters.ChatsAdapter;
import com.beyondthehorizon.associates.adapters.SendingImagesAdapter;
import com.beyondthehorizon.associates.database.ChatModel;
import com.beyondthehorizon.associates.database.CommentsModel;
import com.beyondthehorizon.associates.database.RecentChatModel;
import com.beyondthehorizon.associates.database.SendingImagesModel;
import com.beyondthehorizon.associates.repositories.ChatsRepository;
import com.beyondthehorizon.associates.users.FriendProfileActivity;
import com.beyondthehorizon.associates.groupchat.GroupInfoActivity;
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
import com.squareup.picasso.Picasso;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static com.beyondthehorizon.associates.MainActivity.CHAT_PREFS;
import static com.beyondthehorizon.associates.MainActivity.ChatTypeFromChatsFragment;
import static com.beyondthehorizon.associates.MainActivity.FriendUID;
import static com.beyondthehorizon.associates.MainActivity.MyFriendName;
import static com.beyondthehorizon.associates.MainActivity.ProfileUrlFromChatsFragment;
import static com.beyondthehorizon.associates.util.Constants.Delivered;
import static com.beyondthehorizon.associates.util.Constants.Failed;
import static com.beyondthehorizon.associates.util.Constants.NothingToSend;
import static com.beyondthehorizon.associates.util.Constants.Sending;
import static com.beyondthehorizon.associates.util.Constants.Sent;

public class ChatActivity extends AppCompatActivity implements SendingImagesAdapter.SendMyTxtImage {

    public static final int GALLARY_PICK = 200;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView, mediaRecyclerView;
    public ArrayList<MediaFile> select = new ArrayList<>();
    private ImageButton sendData;
    private EmojiconEditText sampleMessage;
    public static final String TAG = "CHATACTIVITY";
    private ChatsViewModel chatsViewModel;
    private ChatsAdapter chatsAdapter;
    private SendingImagesAdapter imagesAdapter;
    public static final String MY_SHARED_PREF = "shared_prefs";
    private SharedPreferences pref, chatPref;
    private String profileUrl;
    private Toolbar contactsToolbar;
    private TextView userTitle, userOnlineStatus, typingTextView;
    private CircleImageView profile_img;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    ImageView emojiImageView, attachButton;
    View rootView;
    //    Intent intent;
    EmojIconActions emojIcon;
    private StorageReference storageReference;
    String myFriend_Name, friend_Uid, chatType, profile_Uri;
    Dialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        contactsToolbar = findViewById(R.id.ctsToolbar);
        setSupportActionBar(contactsToolbar);

//        intent = getIntent();
        assert getSupportActionBar() != null;
//        getSupportActionBar().setTitle(myFriend_Name);
//        getSupportActionBar().setSubtitle("online");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        pref = getApplicationContext().getSharedPreferences(MY_SHARED_PREF, 0); // 0 - for private mode
        chatPref = getApplicationContext().getSharedPreferences(CHAT_PREFS, 0);

        myFriend_Name = chatPref.getString(MyFriendName, "");
        friend_Uid = chatPref.getString(FriendUID, "");
        chatType = chatPref.getString(ChatTypeFromChatsFragment, "");
        profile_Uri = chatPref.getString(ProfileUrlFromChatsFragment, "");

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("friend_name", myFriend_Name);
        editor.apply();

        attachButton = findViewById(R.id.attachButton);
        typingTextView = findViewById(R.id.typingTextView);
        userTitle = findViewById(R.id.userTitle);
        userOnlineStatus = findViewById(R.id.userOnlineStatus);
        profile_img = findViewById(R.id.imgProfile);
        userTitle.setText(myFriend_Name);

        emojiImageView = findViewById(R.id.emojiButton);
        rootView = findViewById(R.id.root_view);

        dialog2 = new Dialog(ChatActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
        chatsAdapter = new ChatsAdapter(this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatsAdapter);
//        mediaRecyclerView.setAdapter(chatsAdapter);

        mediaRecyclerView = findViewById(R.id.mediaRecyclerView);
        mediaRecyclerView.setHasFixedSize(true);
        mediaRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        sampleMessage = findViewById(R.id.sampleMessage);
        sendData = findViewById(R.id.sendData);


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

        /**SINGLE CHAT */
        if (chatType.contains("Single")) {
            Picasso.get().load(profile_Uri).fit().placeholder(R.drawable.account)
                    .into(profile_img);

            /**GET USER ONLINE STATUS*/
            myRef.child("Users").child("UserProfile").child(friend_Uid).child("onlineStatus")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (!(dataSnapshot.getValue().toString().contains("online") ||
                                        dataSnapshot.getValue().toString().contains("Paused"))) {
                                    Date date = new Date(Long.parseLong(dataSnapshot.getValue().toString()));
//                                    SimpleDateFormat sfd = new SimpleDateFormat("HH:mm a  dd-MM-yyyy");
                                    SimpleDateFormat sfd = new SimpleDateFormat("EEE MMM d ''yy  HH:mm a", Locale.getDefault());
                                    userOnlineStatus.setText("last seen: " + sfd.format(date));
                                    typingTextView.setVisibility(View.GONE);
                                    return;
                                }
                                userOnlineStatus.setText(dataSnapshot.getValue().toString());
                            } else {
                                userOnlineStatus.setText("Offline");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            /** UPDATE MY TYPING.. STATUS*/
            sampleMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() == 0) {
                        myRef.child("Users").child("UserProfile").child(currentUser.getUid()).child("isTyping").setValue(false);
                    } else {
                        myRef.child("Users").child("UserProfile").child(currentUser.getUid()).child("isTyping").setValue(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            /**GET THEIR TYPING STATUS*/
            myRef.child("Users").child("UserProfile").child(friend_Uid)
                    .child("isTyping").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        return;
                    }
                    Log.d(TAG, "onDataChange: " + dataSnapshot.getValue().toString());
                    if (dataSnapshot.getValue().toString().contains("true")) {
                        typingTextView.setVisibility(View.VISIBLE);
                    } else {
                        typingTextView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            /**VIEW FRIEND PROFILE*/
            contactsToolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(ChatActivity.this, FriendProfileActivity.class);
                    intent1.putExtra("userUid", friend_Uid);
                    intent1.putExtra("userName", myFriend_Name);
                    intent1.putExtra("userImage", profile_Uri);
                    startActivity(intent1);
                }
            });

        } else {
            /**GROUP CHAT*/
            userOnlineStatus.setText("tap to view group info");
            Picasso.get().load(profile_Uri)
                    .fit().placeholder(R.drawable.giconn).into(profile_img);

            /**VIEW GROUP DETAILS*/
            contactsToolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(ChatActivity.this, GroupInfoActivity.class);
                    intent1.putExtra("groupUid", friend_Uid);
                    intent1.putExtra("groupName", myFriend_Name);
                    intent1.putExtra("groupImage", profile_Uri);
                    startActivity(intent1);
                }
            });
        }


        myRef.child("Users").child("UserProfile").child(currentUser.getUid())
                .child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.exists())) {
                    startActivity(new Intent(ChatActivity.this, UserProfileActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

        // Write a message to the database
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sampleMessage.getText().toString().trim();
                if (message.isEmpty()) {
                    return;
                }
                Date today = new Date();
                SimpleDateFormat format = new SimpleDateFormat("EEE MMM d ''yy  HH:mm a",
                        Locale.getDefault());
                String dateToStr = format.format(today);
                String msg_key = myRef.child("Users").child("UserChats").push().getKey();

                //Save locally
                chatsViewModel.insertChat(new ChatModel(
                        msg_key,
                        currentUser.getDisplayName(),
                        message,
                        "0",
                        currentUser.getPhoneNumber(),
                        friend_Uid,
                        dateToStr,
                        currentUser.getUid(),
                        profileUrl,
                        NothingToSend,
                        NothingToSend,
                        NothingToSend,
                        NothingToSend,
                        NothingToSend,
                        chatType,
                        Sending));

                //Save locally to view on latest chats
                chatsViewModel.insertLatestChat(new RecentChatModel(
                        friend_Uid,
                        myFriend_Name,
                        message,
                        dateToStr,
                        chatType,
                        profile_Uri));
                sendMessage(message, NothingToSend, NothingToSend,
                        NothingToSend, NothingToSend, dateToStr, msg_key, NothingToSend);

                sampleMessage.setText("");
            }
        });
        Log.d(TAG, "onCreate: " + friend_Uid);
        getChats(friend_Uid);

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMediaStaff();
            }
        });
    }

    private void sendMessage(String message, String imageUrl, String videoUrl, String audioUrl, String fileUrl,
                             String dateToStr, final String msg_key, String docName) {

        ChatModel chatModel = new ChatModel(
                msg_key,
                currentUser.getDisplayName(),
                message,
                "Comments",
                currentUser.getPhoneNumber(),
                currentUser.getUid(),
                dateToStr,
                friend_Uid,
                profileUrl,
                docName,
                imageUrl,
                videoUrl,
                audioUrl,
                fileUrl,
                chatType,
                Sent);

        final String[] deliverySate = new String[1];
        if (chatType.contains("Single")) {
            /**SEND TO SINGLE CHAT FIRE BASE*/
            myRef.child("Users").child("UserChats").child(msg_key)
                    .setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        chatsViewModel.updateDeliveryStatus(msg_key, Delivered);
                        deliverySate[0] = Delivered;
                    } else {
                        chatsViewModel.updateDeliveryStatus(msg_key, Failed);
                        deliverySate[0] = Failed;
                    }
                }
            });

        } else {
            /**SEND TO FIRE BASE ROOM CHAT*/

            myRef.child("Rooms").child(friend_Uid)
                    .child(myFriend_Name).child("UserChats")
                    .child(msg_key).setValue(chatModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                chatsViewModel.updateDeliveryStatus(msg_key, Delivered);
                                deliverySate[0] = Delivered;
                            } else {
                                chatsViewModel.updateDeliveryStatus(msg_key, Failed);
                                deliverySate[0] =Failed;
                            }
                        }
                    });
            chatsViewModel.insertComment(new CommentsModel(
                    msg_key,
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
                    deliverySate[0],
                    "Comment"
            ));
        }
    }

    private void getMediaStaff() {
        //Check Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                ActivityCompat.requestPermissions(ChatActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA}, 100);
                return;
            }
        }
        // custom dialog
        final Dialog dialog = new Dialog(ChatActivity.this);
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
//                Intent intent = new Intent(ChatActivity.this, TakePhotoActivity.class); //Take a photo with a camera
//                startActivityForResult(intent, GALLARY_PICK);
                dialog.dismiss();
            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
                Intent intent = new Intent(ChatActivity.this, FilePickerActivity.class);
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
                startActivityForResult(intent, GALLARY_PICK);
                dialog.dismiss();
            }
        });
        pickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
                Intent intent = new Intent(ChatActivity.this, FilePickerActivity.class);
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
                startActivityForResult(intent, GALLARY_PICK);
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
                Intent intent = new Intent(ChatActivity.this, FilePickerActivity.class);
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
                startActivityForResult(intent, GALLARY_PICK);
                dialog.dismiss();
            }
        });
        pickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
                Intent intent = new Intent(ChatActivity.this, FilePickerActivity.class);
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
                startActivityForResult(intent, GALLARY_PICK);
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

    private void getChats(String friendUID) {
        chatsViewModel.getFriendChats(friendUID).observe(this, new Observer<List<ChatModel>>() {
            @Override
            public void onChanged(List<ChatModel> chatModels) {
                Log.d(TAG, "onChanged: " + chatModels.size());
                if (chatModels.size() > 0) {
                    chatsAdapter.setProMemberArrayList((ArrayList<ChatModel>) chatModels);
                    recyclerView.smoothScrollToPosition(chatModels.size() - 1);
                }
            }
        });
    }

    public void backPressed(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        typingTextView.setVisibility(View.GONE);
        myRef.child("Users").child("UserProfile").child(currentUser.getUid()).child("isTyping").setValue(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_PICK && resultCode == RESULT_OK && data != null) {
            select = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            assert select != null;
            if (select.size() == 0) {
                return;
            }
//            Log.d("TAG", "onActivityResult: " + select.get(0).getPath() + " HERE" + select.get(0).getMediaType());
//            Intent intent = new Intent(ChatActivity.this, SendingMediaActivity.class);
//            intent.putExtra("ikosawa", select);
//            startActivity(intent);

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

    @Override
    protected void onResume() {
        super.onResume();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
    }


    public void sendMediaContent(final String mediaName, final String message, String imageUrl,
                                 String videoUrl, String audioUrl, String fileUrl, String msg_key,
                                 String dateToStr) {

        //GET MEDIATYPE FROM MEDIAFILE
        //Save locally
        chatsViewModel.insertChat(new ChatModel(
                msg_key,
                currentUser.getDisplayName(),
                message,
                "0",
                currentUser.getPhoneNumber(),
                friend_Uid,
                dateToStr,
                currentUser.getUid(),
                profileUrl,
                mediaName,
                imageUrl,
                videoUrl,
                audioUrl,
                fileUrl,
                chatType,
                Sending));

        //Save locally to view on latest chats
        chatsViewModel.insertLatestChat(new RecentChatModel(
                friend_Uid,
                myFriend_Name,
                message,
                dateToStr,
                chatType,
                profileUrl));
    }


    @Override
    public void sendTextImage(ArrayList<SendingImagesModel> arrayList, String imageUrl, String videoUrl,
                              String audioUrl, String fileUrl, final String profileUrl, final String friend_Uid,
                              final String chatType, String myFriend_Name) {
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference();
//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
//        storageReference = FirebaseStorage.getInstance().getReference();
//        ChatsRepository chatsRepository = new ChatsRepository(getApplication());
////        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
//
//        for (final SendingImagesModel model : arrayList) {
//            Log.d(TAG, "sendTextImage: " + model.getImageUri() + " " + model.getTxtMessage());
//            final String msg_key = myRef.child("Users").child("UserChats").push().getKey();
//            final StorageReference filePath;
//
//            Date today = new Date();
//            SimpleDateFormat format = new SimpleDateFormat("EEE MMM d ''yy  HH:mm a",
//                    Locale.getDefault());
//            final String dateToStr = format.format(today);
////
////            Log.d(TAG, "sendTextImage: " + friend_Uid + " " +
////                    dateToStr + " " +
////                    currentUser.getUid() + " " +
////                    profileUrl + " " +
////                    imageUrl + " " +
////                    videoUrl + " " +
////                    audioUrl + " " +
////                    fileUrl + " " +
////                    chatType);
//            //GET MEDIATYPE FROM MEDIAFILE
//            //Save locally
//            chatsRepository.insertChat(new ChatModel(
//                    msg_key,
//                    currentUser.getDisplayName(),
//                    model.getTxtMessage(),
//                    "0",
//                    currentUser.getPhoneNumber(),
//                    friend_Uid,
//                    dateToStr,
//                    currentUser.getUid(),
//                    profileUrl,
//                    model.getMediaFile().getName(),
//                    imageUrl,
//                    videoUrl,
//                    audioUrl,
//                    fileUrl,
//                    chatType,
//                    Sending));
//
//            //Save locally to view on latest chats
//            chatsRepository.insertLatestChat(new RecentChatModel(
//                    friend_Uid,
//                    myFriend_Name,
//                    model.getTxtMessage(),
//                    dateToStr,
//                    chatType,
//                    profileUrl));
//
//            if (model.getMediaType().contains("0")) {
////                sendTextImage(imagesAdapter.sendingImagesModelArrayList,
////                        NothingToSend, NothingToSend, NothingToSend, model.getImageUri());
//
//            } else if (model.getMediaType().contains("1")) {
//
//                filePath = storageReference.child("Chat Images").child(msg_key + ".jpg");
//                filePath.putFile(Uri.fromFile(new File(model.getImageUri()))).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Log.d(TAG, "onSuccess: " + uri.toString());
//                                sendMessage(model.getTxtMessage(), uri.toString(), NothingToSend,
//                                        NothingToSend, NothingToSend, dateToStr, msg_key,
//                                        friend_Uid, chatType, profileUrl, model.getMediaFile().getName());
//                            }
//                        });
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: " + e.getMessage());
//                    }
//                });
//
//            } else if (model.getMediaType().contains("2")) {
////                sendTextImage(imagesAdapter.sendingImagesModelArrayList,
////                        NothingToSend, NothingToSend, model.getImageUri(), NothingToSend);
//
//            } else if (model.getMediaType().contains("3")) {
//
////                sendTextImage(imagesAdapter.sendingImagesModelArrayList,
////                        NothingToSend, model.getImageUri(), NothingToSend, NothingToSend);
//            }
//
//        }
////        dialog2.dismiss();
////        arrayList.clear();
    }

}