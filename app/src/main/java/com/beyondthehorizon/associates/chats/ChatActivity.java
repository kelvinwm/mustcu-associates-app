package com.beyondthehorizon.associates.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.adapters.ChatsAdapter;
import com.beyondthehorizon.associates.database.ChatModel;
import com.beyondthehorizon.associates.database.CommentsModel;
import com.beyondthehorizon.associates.database.RecentChatModel;
import com.beyondthehorizon.associates.users.FriendProfileActivity;
import com.beyondthehorizon.associates.users.GroupInfoActivity;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    //    private Button sendData, sendGroup;
    private ImageButton sendData;
    private EmojiconEditText sampleMessage;
    public static final String TAG = "CHATACTIVITY";
    private ChatsViewModel chatsViewModel;
    private ChatsAdapter chatsAdapter;
    public static final String MY_SHARED_PREF = "shared_prefs";
    SharedPreferences pref;
    String imageUrl;
    private Toolbar contactsToolbar;
    private TextView userTitle, userOnlineStatus, typingTextView;
    private CircleImageView profile_img;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    ImageView emojiImageView;
    View rootView;
    EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        contactsToolbar = findViewById(R.id.ctsToolbar);
        setSupportActionBar(contactsToolbar);

        final Intent intent = getIntent();
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatsAdapter);
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
                                    SimpleDateFormat sfd = new SimpleDateFormat("HH:mm a  dd-MM-yyyy");
                                    userOnlineStatus.setText("last seen " + sfd.format(date));
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
                    imageUrl = dataSnapshot.getValue().toString();
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
                SimpleDateFormat format = new SimpleDateFormat("HH:mm a      yyyy-MM-dd");
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
                        imageUrl,
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
                        imageUrl,
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
        });
        Log.d(TAG, "onCreate: " + intent.getStringExtra("friendUID"));
        getChats(intent.getStringExtra("friendUID"));
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
}