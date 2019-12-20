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
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.adapters.ChatsAdapter;
import com.beyondthehorizon.associates.adapters.SendingImagesAdapter;
import com.beyondthehorizon.associates.database.ChatModel;
import com.beyondthehorizon.associates.database.CommentsModel;
import com.beyondthehorizon.associates.database.RecentChatModel;
import com.beyondthehorizon.associates.database.SendingImagesModel;
import com.beyondthehorizon.associates.users.FriendProfileActivity;
import com.beyondthehorizon.associates.users.GroupInfoActivity;
import com.beyondthehorizon.associates.users.UserProfileActivity;
import com.beyondthehorizon.associates.viewmodels.ChatsViewModel;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.TakePhotoActivity;
import com.dmcbig.mediapicker.entity.Media;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity implements SendingImagesAdapter.SendMyTxtImage {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView, mediaRecyclerView;
    private ArrayList<Media> select = new ArrayList<>();
    private ImageButton sendData;
    private EmojiconEditText sampleMessage;
    public static final String TAG = "CHATACTIVITY";
    private ChatsViewModel chatsViewModel;
    private ChatsAdapter chatsAdapter;
    private SendingImagesAdapter imagesAdapter;
    public static final String MY_SHARED_PREF = "shared_prefs";
    SharedPreferences pref;
    String profileUrl;
    private Toolbar contactsToolbar;
    private TextView userTitle, userOnlineStatus, typingTextView;
    private CircleImageView profile_img;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    ImageView emojiImageView, attachButton;
    View rootView;
    Intent intent;
    EmojIconActions emojIcon;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        contactsToolbar = findViewById(R.id.ctsToolbar);
        setSupportActionBar(contactsToolbar);

        intent = getIntent();
        assert getSupportActionBar() != null;
//        getSupportActionBar().setTitle(intent.getStringExtra("myFriendName"));
//        getSupportActionBar().setSubtitle("online");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        pref = getApplicationContext().getSharedPreferences(MY_SHARED_PREF, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("friend_name", intent.getStringExtra("myFriendName"));
        editor.apply();

        attachButton = findViewById(R.id.attachButton);
        typingTextView = findViewById(R.id.typingTextView);
        userTitle = findViewById(R.id.userTitle);
        userOnlineStatus = findViewById(R.id.userOnlineStatus);
        profile_img = findViewById(R.id.imgProfile);
        userTitle.setText(intent.getStringExtra("myFriendName"));

        emojiImageView = (ImageView) findViewById(R.id.emojiButton);
        rootView = findViewById(R.id.root_view);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
        chatsAdapter = new ChatsAdapter(this);


        storageReference = FirebaseStorage.getInstance().getReference().child("Chat Images");
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
        if (intent.getStringExtra("chatTypeFromChatsFragment").contains("Single")) {
            Picasso.get().load(intent.getStringExtra("imageUrlFromChatsFragment")).fit().placeholder(R.drawable.account)
                    .into(profile_img);

            /**GET USER ONLINE STATUS*/
            myRef.child("Users").child("UserProfile").child(intent.getStringExtra("friendUID")).child("onlineStatus")
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
            myRef.child("Users").child("UserProfile").child(intent.getStringExtra("friendUID"))
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
                    intent1.putExtra("userUid", intent.getStringExtra("friendUID"));
                    intent1.putExtra("userName", intent.getStringExtra("myFriendName"));
                    intent1.putExtra("userImage", intent.getStringExtra("imageUrlFromChatsFragment"));
                    startActivity(intent1);
                }
            });

        } else {
            /**GROUP CHAT*/
            userOnlineStatus.setText("tap to view group info");
            Picasso.get().load(intent.getStringExtra("imageUrlFromChatsFragment"))
                    .fit().placeholder(R.drawable.giconn).into(profile_img);

            /**VIEW GROUP DETAILS*/
            contactsToolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(ChatActivity.this, GroupInfoActivity.class);
                    intent1.putExtra("groupUid", intent.getStringExtra("friendUID"));
                    intent1.putExtra("groupName", intent.getStringExtra("myFriendName"));
                    intent1.putExtra("groupImage", intent.getStringExtra("imageUrlFromChatsFragment"));
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
                sendMessage();
            }
        });
        Log.d(TAG, "onCreate: " + intent.getStringExtra("friendUID"));
        getChats(intent.getStringExtra("friendUID"));


        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMediaStaff();
            }
        });
    }

    private void sendMessage() {
        String message = sampleMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d ''yy  HH:mm a", Locale.getDefault());
        String dateToStr = format.format(today);

        final String msg_key = myRef.child("Users").child("UserChats").push().getKey();
        ChatModel chatModel = new ChatModel(
                msg_key,
                currentUser.getDisplayName(),
                message,
                "Comments",
                currentUser.getPhoneNumber(),
                currentUser.getUid(),
                dateToStr,
                intent.getStringExtra("friendUID"),
                profileUrl,
                "*hak*none0#",
                intent.getStringExtra("chatTypeFromChatsFragment"),
                "sent");

        if (intent.getStringExtra("chatTypeFromChatsFragment").contains("Single")) {
            /**SEND TO SINGLE CHAT FIRE BASE*/
            myRef.child("Users").child("UserChats").child(msg_key)
                    .setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        chatsViewModel.updateDeliveryStatus(msg_key, "Delivered");
                    } else {
                        chatsViewModel.updateDeliveryStatus(msg_key, "Failed");
                    }
                }
            });

        } else {
            /**SEND TO FIRE BASE ROOM CHAT*/
            myRef.child("Rooms").child(intent.getStringExtra("friendUID"))
                    .child(intent.getStringExtra("myFriendName")).child("UserChats")
                    .child(msg_key).setValue(chatModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                chatsViewModel.updateDeliveryStatus(msg_key, "Delivered");
                            } else {
                                chatsViewModel.updateDeliveryStatus(msg_key, "Failed");
                            }
                        }
                    });


            chatsViewModel.insertCommentt(new CommentsModel(
                    msg_key,
                    currentUser.getDisplayName(),
                    message,
                    currentUser.getPhoneNumber(),
                    currentUser.getUid(),
                    dateToStr,
                    "Comment"
            ));
        }

        //Save locally
        chatsViewModel.insertChat(new ChatModel(
                msg_key,
                currentUser.getDisplayName(),
                message,
                "0",
                currentUser.getPhoneNumber(),
                intent.getStringExtra("friendUID"),
                dateToStr,
                currentUser.getUid(),
                profileUrl,
                "*hak*none0#",
                intent.getStringExtra("chatTypeFromChatsFragment"),
                "sending..."));

        //Save locally to view on latest chats
        chatsViewModel.insertLatestChat(new RecentChatModel(
                intent.getStringExtra("friendUID"),
                intent.getStringExtra("myFriendName"),
                message,
                dateToStr,
                intent.getStringExtra("chatTypeFromChatsFragment"),
                intent.getStringExtra("imageUrlFromChatsFragment")));
        sampleMessage.setText("");
    }

    private void getMediaStaff() {
        //Check Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                ActivityCompat.requestPermissions(ChatActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
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
                Intent intent = new Intent(ChatActivity.this, TakePhotoActivity.class); //Take a photo with a camera
                startActivityForResult(intent, 200);
                dialog.dismiss();
            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
                Intent intent = new Intent(ChatActivity.this, PickerActivity.class);
                intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE); // Set the selection type, the default is that pictures and videos can be selected together (optional parameters)
                long maxSize = 188743680L; // long long long long
                intent.putExtra(PickerConfig.MAX_SELECT_SIZE, maxSize); // Maximum selection size, default 180M (optional parameter)
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 15); // Maximum number of choices, default 40 (optional parameter)
                ArrayList<Media> defaultSelect = select; // You can set the photos selected by default, such as setting the list you just selected to the default.
                intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, defaultSelect); // You can set the photo selected by default (optional parameter)
                startActivityForResult(intent, 200);
                dialog.dismiss();
            }
        });
        pickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
                Intent intent = new Intent(ChatActivity.this, PickerActivity.class);
                intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_VIDEO); // Set the selection type, the default is that pictures and videos can be selected together (optional parameters)
                long maxSize = 188743680L; // long long long long
                intent.putExtra(PickerConfig.MAX_SELECT_SIZE, maxSize); // Maximum selection size, default 180M (optional parameter)
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, 15); // Maximum number of choices, default 40 (optional parameter)
                ArrayList<Media> defaultSelect = select; // You can set the photos selected by default, such as setting the list you just selected to the default.
                intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, defaultSelect); // You can set the photo selected by default (optional parameter)
                startActivityForResult(intent, 200);
                dialog.dismiss();
            }
        });
        pickAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
                dialog.dismiss();
            }
        });
        pickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: SAVE LOCATION TO DATABASE
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, 1);
                dialog.dismiss();
            }
        });

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
        if (requestCode == 200 && resultCode == PickerConfig.RESULT_CODE && data != null) {
            select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);//选择完后返回的list
            mediaRecyclerView.setVisibility(View.VISIBLE);

            ArrayList<SendingImagesModel> allMedia = new ArrayList<>();
            LinearLayout L123 = findViewById(R.id.L123);
            L123.setVisibility(View.GONE);

            for (Media uri : select) {
                Log.d(TAG, "onActivityResult: " + Uri.fromFile(new File(uri.path)));
                allMedia.add(new SendingImagesModel(Uri.fromFile(new File(uri.path)), ""));

                imagesAdapter = new SendingImagesAdapter(ChatActivity.this, allMedia, this);
                mediaRecyclerView.setAdapter(imagesAdapter);

            }

        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //the selected audio.
            Uri uri = data.getData();
        }
    }

    @Override
    public void sendTextImage(ArrayList<SendingImagesModel> arrayList) {
        for (SendingImagesModel model : arrayList) {
            Log.d(TAG, "sendTextImage: " + model.getImageUri().toString() + " " + model.getTxtMessage());

            final StorageReference filePath = storageReference.child(currentUser.getUid() + ".jpg");
//                filePath.putFile(Uri.fromFile(new File(uri.path))).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Log.d(TAG, "onSuccess: " + uri.toString());
////                                myRef.child("Users").child("UserProfile").child(currentUser.getUid())
////                                        .child("imageUrl").setValue(uri.toString());
////                                profile_image.setImageURI(resultUri);
////                                progressDialog.dismiss();
//                            }
//                        });
//                    }
//                });

            mediaRecyclerView.setVisibility(View.GONE);
            LinearLayout L123 = findViewById(R.id.L123);
            L123.setVisibility(View.VISIBLE);

            arrayList.clear();
        }
    }
}