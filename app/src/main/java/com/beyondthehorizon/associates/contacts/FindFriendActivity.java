package com.beyondthehorizon.associates.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.database.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindFriendActivity extends AppCompatActivity {
    private static final String TAG = "ADDGROUP";
    private FirebaseAuth mAuth;
    private List<UserProfile> userChat;
    private RecyclerView recyclerView;
    private FindFriendAdapter findFriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.findFriendRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        userChat = new ArrayList<>();
        myRef.keepSynced(true);
        myRef.child("Users").child("UserProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userChat.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserProfile userProfile = new UserProfile(
                            postSnapshot.child("userToken").getValue().toString(),
                            postSnapshot.child("userUid").getValue().toString(),
                            postSnapshot.child("userName").getValue().toString(),
                            postSnapshot.child("phoneNumber").getValue().toString(),
                            postSnapshot.child("imageUrl").getValue().toString(),
                            postSnapshot.child("tagLine").getValue().toString()
                    );
                    if (!userProfile.getUserUid().contains(currentUser.getUid())) {
                        userChat.add(userProfile);
                    }
                }
                findFriendAdapter = new FindFriendAdapter(FindFriendActivity.this,
                        (ArrayList<UserProfile>) userChat);
                recyclerView.setAdapter(findFriendAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
