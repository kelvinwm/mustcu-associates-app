package com.beyondthehorizon.testfirebasefunctionstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.beyondthehorizon.testfirebasefunctionstest.ViewModels.ChatsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final Intent intent = getIntent();
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(intent.getStringExtra("myFriend"));
        mAuth = FirebaseAuth.getInstance();

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

        // Write a message to the database
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sampleMessage.getText().toString().trim();
                if (message.isEmpty()) {
                    return;
                }
                long tsLong = System.currentTimeMillis() / 1000;
                String timestamp = Long.toString(tsLong);
//                myRef.child("Rooms").child("Family").child("UserChats").push().setValue(new ChatModel(message));
                ChatModel chatModel = new ChatModel(
                        currentUser.getDisplayName(), message,
                        currentUser.getPhoneNumber(),
                        currentUser.getUid(),
                        timestamp,
                        intent.getStringExtra("friendUID"));


                myRef.child("Users").child("UserChats").push()
                        .setValue(chatModel);

                //Save locally
                chatsViewModel.insertChat(new ChatModel(
                        currentUser.getDisplayName(), message,
                        currentUser.getPhoneNumber(),
                        intent.getStringExtra("friendUID"),
                        timestamp,
                        currentUser.getUid()));
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
                }
            }
        });
    }
}
