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
import com.beyondthehorizon.associates.adapters.CommentsAdapter;
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
import java.util.Date;
import java.util.List;

import static com.beyondthehorizon.associates.chats.ChatActivity.MY_SHARED_PREF;

public class CommentsChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    //    private Button sendData, sendGroup;
    private ImageButton sendData;
    private EditText sampleMessage, createGroup;
    public static final String TAG = "COMMENTSACTIVITY";
    private ChatsViewModel chatsViewModel;
    private CommentsAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_chat);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Comments");
        mAuth = FirebaseAuth.getInstance();
        final Intent intent = getIntent();
        final SharedPreferences pref = getApplicationContext().getSharedPreferences(MY_SHARED_PREF, 0);

        recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setHasFixedSize(true);
        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
        commentsAdapter = new CommentsAdapter(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(commentsAdapter);
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
                SimpleDateFormat format = new SimpleDateFormat("HH:mm a      yyyy-MM-dd");
                String dateToStr = format.format(today);
//                long tsLong = System.currentTimeMillis() / 1000;
//                String timestamp = Long.toString(tsLong);
//                myRef.child("Rooms").child("Family").child("UserChats").push().setValue(new ChatModel(message));

                String msg_key = myRef.child("Users").child("UserComments").push().getKey();
                CommentsModel commentsModel = new CommentsModel(
                        intent.getStringExtra("MainQuestionKey"),
                        currentUser.getDisplayName(),
                        message,
                        currentUser.getPhoneNumber(),
                        currentUser.getUid(),
                        dateToStr,
                        "Comment");

                /**SEND TO FIRE BASE ROOM CHAT COMMENTS*/
                myRef.child("Rooms").child(intent.getStringExtra("RoomId"))
                        .child(pref.getString("friend_name", null))
                        .child("UserComments").child(intent.getStringExtra("MainQuestionKey"))
                        .child(msg_key).setValue(commentsModel);

                //Save locally
                chatsViewModel.insertCommentt(new CommentsModel(
                        intent.getStringExtra("MainQuestionKey"),
                        currentUser.getDisplayName(),
                        message,
                        currentUser.getPhoneNumber(),
                        currentUser.getUid(),
                        dateToStr,
                        "Comment"));

                sampleMessage.setText("");
            }
        });
        getComments(intent.getStringExtra("MainQuestionKey"));
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
}