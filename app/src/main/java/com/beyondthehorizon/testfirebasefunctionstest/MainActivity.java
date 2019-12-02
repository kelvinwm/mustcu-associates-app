package com.beyondthehorizon.testfirebasefunctionstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.beyondthehorizon.testfirebasefunctionstest.ViewModels.ChatsViewModel;
import com.beyondthehorizon.testfirebasefunctionstest.database.RecentChatModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    public static final String TAG = "MAINACTIVITY";
    private String token;
    private AllUsersAdapter allUsersAdapter;
    private List<UserProfile> userChat;
    private ChatsViewModel chatsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setHasFixedSize(true);
        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));


        final FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            //To do
                            return;
                        }
                        // Get the Instance ID token//
                        token = task.getResult().getToken();
                        Log.d(TAG, token);
                        myRef.child("Users").child("UserProfile").child(currentUser.getUid())
                                .child("userToken").setValue(token);

                    }
                });

        myRef.child("Users").child("UserProfile").child(currentUser.getUid())
                .child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.exists())) {
                    startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        // Create a group
//        sendGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String groupName = createGroup.getText().toString().trim();
//                if (groupName.isEmpty()) {
//                    return;
//                }
//                myRef.child("Rooms").child(groupName).child("UserTokens").child(currentUser.getUid()).setValue(token);
//                createGroup.setText("");
//            }
//        });

        userChat = new ArrayList<>();

//        myRef.child("Users").child("UserProfile").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//
//                    UserProfile userProfile = postSnapshot.getValue(UserProfile.class);
//                    userChat.add(userProfile);
//                }
//                allUsersAdapter = new AllUsersAdapter(MainActivity.this,
//                        (ArrayList<UserProfile>) userChat);
//                recyclerView.setAdapter(allUsersAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        getAllLatestChats();
    }

    private void getAllLatestChats() {
        chatsViewModel.getAllLatestChats().observe(this, new Observer<List<RecentChatModel>>() {
            @Override
            public void onChanged(List<RecentChatModel> recentChatModels) {
                Log.d(TAG, "onChanged: " + recentChatModels.size());
                if (recentChatModels.size() > 0) {

                    allUsersAdapter = new AllUsersAdapter(MainActivity.this,
                            (ArrayList<RecentChatModel>) recentChatModels);
                    recyclerView.setAdapter(allUsersAdapter);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser currentUser = mAuth.getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference();
                myRef.child("Users").child("UserProfile").child(currentUser.getUid())
                        .child("userName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!(dataSnapshot.exists())) {
                            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_main_setting:
//                addSomething();
                return true;
            case R.id.menu_main_add_group:
                addGroup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addGroup() {
    }
}
