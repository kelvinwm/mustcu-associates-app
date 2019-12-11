package com.beyondthehorizon.associates.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.contacts.UserContactsAdapter;
import com.beyondthehorizon.associates.database.GroupInfo;
import com.beyondthehorizon.associates.database.GroupUserInfo;
import com.beyondthehorizon.associates.database.RecentChatModel;
import com.beyondthehorizon.associates.database.UserProfile;
import com.beyondthehorizon.associates.viewmodels.ChatsViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupActivity extends AppCompatActivity {

    private static final String TAG = "ADDGROUP";
    private static final int RESULT_LOAD_IMAGE = 102;
    private FirebaseAuth mAuth;
    private List<UserProfile> userChat;
    private RecyclerView recyclerView;
    private UserContactsAdapter userContactsAdapter;
    private ImageView createGroup;
    private CircleImageView choose_group_image;
    private EditText group_name;
    private ChatsViewModel chatsViewModel;
    private final int PICK_IMAGE_REQUEST = 71;
    private StorageReference storageReference;
    private Uri resultUri = null;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;
    private  ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
        currentUser = mAuth.getCurrentUser();
        group_name = findViewById(R.id.group_name);
        recyclerView = findViewById(R.id.myContacts);
        choose_group_image = findViewById(R.id.choose_group_image);
        createGroup = findViewById(R.id.createGroupButton);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        progressDialog = new ProgressDialog(AddGroupActivity.this);
        progressDialog.setTitle("Creating Group...");
        progressDialog.setMessage("Please wait....");

        userChat = new ArrayList<>();
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
                userContactsAdapter = new UserContactsAdapter(AddGroupActivity.this,
                        (ArrayList<UserProfile>) userChat);
                recyclerView.setAdapter(userContactsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        choose_group_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
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

                progressDialog.show();

                final String numOfUsers = String.valueOf(userContactsAdapter.allTokens.size() + 1);
                final String myKey = myRef.child("Rooms").push().getKey();
                final StorageReference filePath1 = storageReference.child("Group Profile Images").child(myKey + ".jpg");

                myRef.child("Rooms").child(myKey).setValue(groupName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (!(resultUri == null)) {
                            filePath1.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    filePath1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(TAG, "onSuccess: " + uri.toString());
                                            /**SET ADMIN INFO*/
                                            myRef.child("Rooms").child(myKey).child(groupName).child("UserTokens")
                                                    .child(currentUser.getUid()).setValue(new GroupUserInfo(token[0],
                                                    "Admin", currentUser.getPhoneNumber(), uri.toString()));
                                            /**SET ALL USERS INFO*/
                                            for (UserProfile profile : userContactsAdapter.allTokens) {
                                                myRef.child("Rooms").child(myKey).child(groupName).child("UserTokens")
                                                        .child(profile.getUserUid()).setValue(new GroupUserInfo(profile.getUserToken(),
                                                        "Member", currentUser.getPhoneNumber(), uri.toString()));
                                            }
                                            setGroupInfo(uri.toString(), myKey, groupName, numOfUsers);
                                        }
                                    });
                                }
                            });
                        } else {
                            /**SET GROUP INFO*/
                            /**SET ADMIN INFO*/
                            myRef.child("Rooms").child(myKey).child(groupName).child("UserTokens")
                                    .child(currentUser.getUid()).setValue(new GroupUserInfo(token[0],
                                    "Admin", currentUser.getPhoneNumber(), "none"));

                            /**SET ALL USERS INFO*/
                            for (UserProfile profile : userContactsAdapter.allTokens) {
                                myRef.child("Rooms").child(myKey).child(groupName).child("UserTokens")
                                        .child(profile.getUserUid()).setValue(new GroupUserInfo(profile.getUserToken(),
                                        "Member", currentUser.getPhoneNumber(), "none"));
                            }

                            setGroupInfo("None", myKey, groupName, numOfUsers);
                        }

                    }
                });

            }
        });
    }

    private void setGroupInfo(String imageUri, String myKey, String groupName, String numberOfMembers) {

        /**SET GROUP INFO*/
        myRef.child("Rooms").child(myKey).child(groupName).child("GroupInfo").setValue(new GroupInfo(
                imageUri, numberOfMembers));
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss a      yyyy-MM-dd");
        String dateToStr = format.format(today);

        //Save Group locally to view on latest chats
        chatsViewModel.insertLatestChat(new RecentChatModel(
                myKey, groupName,
                "Created new group " + groupName,
                dateToStr, "Room", ""));
        group_name.setText("");
        progressDialog.dismiss();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                choose_group_image.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
            }
        }
    }
}