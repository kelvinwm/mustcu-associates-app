package com.beyondthehorizon.testfirebasefunctionstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beyondthehorizon.testfirebasefunctionstest.database.RecentChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity {

    private static final String TAG = "ADDGROUP";
    private FirebaseAuth mAuth;
    private List<UserProfile> userChat;
    private RecyclerView recyclerView;
    private UserContactsAdapter userContactsAdapter;
    private Button createGroup;
    EditText group_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        group_name = findViewById(R.id.group_name);
        recyclerView = findViewById(R.id.myContacts);
        createGroup = findViewById(R.id.createGroupButton);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        userChat = new ArrayList<>();
        myRef.child("Users").child("UserProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    UserProfile userProfile = postSnapshot.getValue(UserProfile.class);
                    if(!userProfile.userUid.contains(currentUser.getUid())){
                        userChat.add(userProfile);
                    }
                }
                userContactsAdapter = new UserContactsAdapter(AddGroupActivity.this,
                        (ArrayList<UserProfile>) userChat);
                recyclerView.setAdapter(userContactsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String groupName = group_name.getText().toString().trim();
                if (groupName.isEmpty()) {
                    group_name.setError("Enter a group name");
                    return;
                }
                if (userContactsAdapter.allTokens.size() == 0) {
                    Toast.makeText(AddGroupActivity.this, "Choose at least one participant", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String myKey = myRef.child("Rooms").push().getKey();
                myRef.child("Rooms").child(myKey).setValue(groupName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        for (UserProfile profile : userContactsAdapter.allTokens) {
                            myRef.child("Rooms").child(myKey).child(groupName).child("UserTokens")
                                    .child(profile.userUid).setValue(profile.getUserToken());
                        }
                        group_name.setText("");
                        finish();
                    }
                });

            }
        });
    }
}
