package com.beyondthehorizon.associates.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.adapters.ChatsAdapter;
import com.beyondthehorizon.associates.database.ChatModel;
import com.beyondthehorizon.associates.database.CommentsModel;
import com.beyondthehorizon.associates.database.RecentChatModel;
import com.beyondthehorizon.associates.users.UserProfileActivity;
import com.beyondthehorizon.associates.viewmodels.ChatsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    //    private Button sendData, sendGroup;
    private ImageButton sendData;
    private EditText sampleMessage, createGroup;
    public static final String TAG = "CHATACTIVITY";
    private ChatsViewModel chatsViewModel;
    private ChatsAdapter chatsAdapter;
    public static final String MY_SHARED_PREF = "shared_prefs";
    SharedPreferences pref;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final Intent intent = getIntent();
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(intent.getStringExtra("myFriendName"));
        mAuth = FirebaseAuth.getInstance();
        pref = getApplicationContext().getSharedPreferences(MY_SHARED_PREF, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("friend_name", intent.getStringExtra("myFriendName"));
        editor.apply();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
        chatsAdapter = new ChatsAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatsAdapter);
        sampleMessage = findViewById(R.id.sampleMessage);
//        createGroup = findViewById(R.id.createGroup);
//        sampleMessage = findViewById(R.id.sampleMessage);
//        sendGroup = findViewById(R.id.sendGroup);
        sendData = findViewById(R.id.sendData);

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();


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
//                long tsLong = System.currentTimeMillis() / 1000;
//                String timestamp = Long.toString(tsLong);
//                myRef.child("Rooms").child("Family").child("UserChats").push().setValue(new ChatModel(message));

                String msg_key = myRef.child("Users").child("UserChats").push().getKey();
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
                        intent.getStringExtra("chatTypeFromChatsFragment"));

                if (intent.getStringExtra("chatTypeFromChatsFragment").contains("Single")) {
                    /**SEND TO SINGLE CHAT FIRE BASE*/
                    myRef.child("Users").child("UserChats").child(msg_key)
                            .setValue(chatModel);
                } else {
                    /**SEND TO FIRE BASE ROOM CHAT*/
                    myRef.child("Rooms").child(intent.getStringExtra("friendUID"))
                            .child(intent.getStringExtra("myFriendName")).child("UserChats")
                            .child(msg_key).setValue(chatModel);


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
                        "Comments",
                        currentUser.getPhoneNumber(),
                        intent.getStringExtra("friendUID"),
                        dateToStr,
                        currentUser.getUid(),
                        imageUrl,
                        intent.getStringExtra("chatTypeFromChatsFragment")));

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
}