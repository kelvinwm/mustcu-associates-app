package com.beyondthehorizon.associates.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.contacts.UserContactsAdapter;
import com.beyondthehorizon.associates.database.GroupInfo;
import com.beyondthehorizon.associates.database.RecentChatModel;
import com.beyondthehorizon.associates.database.UserProfile;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddGroupActivity extends AppCompatActivity {

    private static final String TAG = "ADDGROUP";
    private FirebaseAuth mAuth;
    private List<UserProfile> userChat;
    private RecyclerView recyclerView;
    private UserContactsAdapter userContactsAdapter;
    private Button createGroup;
    EditText group_name;
    private ChatsViewModel chatsViewModel;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
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
                    if (!userProfile.getUserUid().contains(currentUser.getUid())) {
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
                createGroup.setEnabled(false);
                final String groupName = group_name.getText().toString().trim();
                if (groupName.isEmpty()) {
                    group_name.setError("Enter a group name");
                    return;
                }
                if (userContactsAdapter.allTokens.size() == 0) {
                    Toast.makeText(AddGroupActivity.this, "Choose at least one participant", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String[] token = new String[1];
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    //To do
                                    return;
                                }
                                // Get the Instance ID token//
                                token[0] = task.getResult().getToken();

                            }
                        });
                final ProgressDialog progressDialog = new ProgressDialog(AddGroupActivity.this);
                progressDialog.setTitle("Creating Group...");
                progressDialog.show();
                final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
                final String myKey = myRef.child("Rooms").push().getKey();
//                Uri file = Uri.fromFile(new File(String.valueOf(R.drawable.ic_group)));
//                ref.putFile(file)
//                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                progressDialog.dismiss();
//                                Toast.makeText(AddGroupActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
//                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        Log.d(TAG, "onSuccess: uri= " + uri.toString());
//                                    }
//                                });
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                progressDialog.dismiss();
//                                Toast.makeText(AddGroupActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
//                                        .getTotalByteCount());
//                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
//                            }
//                        });

                myRef.child("Rooms").child(myKey).setValue(groupName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        myRef.child("Rooms").child(myKey).child(groupName).child("UserTokens")
                                .child(currentUser.getUid()).setValue(new GroupInfo(token[0],
                                "Admin", currentUser.getPhoneNumber()));
                        for (UserProfile profile : userContactsAdapter.allTokens) {
                            myRef.child("Rooms").child(myKey).child(groupName).child("UserTokens")
                                    .child(profile.getUserUid()).setValue(new GroupInfo(profile.getUserToken(),
                                    "Member", currentUser.getPhoneNumber()));
                        }
                        Date today = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss a      yyyy-MM-dd");
                        String dateToStr = format.format(today);

                        //Save Group locally to view on latest chats
                        chatsViewModel.insertLatestChat(new RecentChatModel(
                                myKey, groupName,
                                "You created a new group " + groupName + " " + dateToStr,
                                dateToStr, "Room", ""));
                        group_name.setText("");
                        finish();
                    }
                });

            }
        });
    }
}