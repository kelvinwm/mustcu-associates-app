package com.beyondthehorizon.associates.users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondthehorizon.associates.MainActivity;
import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.SplashActivity;
import com.beyondthehorizon.associates.database.UserProfile;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfile";
    private Button updateName;
    private EditText userName, tagLine;
    private TextView phoneNumber;
    private FirebaseAuth mAuth;
    private ImageView changeProfile, editUserName, editTagLine;
    private RelativeLayout R2;
    private CircleImageView profile_image;
    public static final int GALLARY_PICK = 1;
    String token;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        userName = findViewById(R.id.userName);
        updateName = findViewById(R.id.updateName);

        tagLine = findViewById(R.id.tagLine);
        phoneNumber = findViewById(R.id.phoneNumber);
        changeProfile = findViewById(R.id.changeProfile);
        editUserName = findViewById(R.id.editUserName);
        editTagLine = findViewById(R.id.editTagLine);

        R2 = findViewById(R.id.R2);
        profile_image = findViewById(R.id.profile_image);

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getGallary = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(getGallary, GALLARY_PICK);
            }
        });
        //ACTIVATE BUTTON AND EDIT TEXTS
        editUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName.setEnabled(true);
                userName.setEnabled(true);
                userName.requestFocus();

            }
        });
        editTagLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateName.setEnabled(true);
                tagLine.setEnabled(true);
                tagLine.requestFocus();
            }
        });
        phoneNumber.setText(currentUser.getPhoneNumber());
        if (!(currentUser.getDisplayName() == null)) {
            userName.setText(currentUser.getDisplayName());
        }
        myRef.child("Users").child("UserProfile").child(currentUser.getUid()).child("tagLine")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tagLine.setText(dataSnapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        myRef.child("Users").child("UserProfile").child(currentUser.getUid()).child("imageUrl")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Picasso.get().load(dataSnapshot.getValue().toString()).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    R2.setBackground(new BitmapDrawable(bitmap));
                                    Picasso.get().load(currentUser.getPhotoUrl().toString())
                                            .fit().placeholder(R.drawable.loader).error(R.drawable.profile_mask).into(profile_image);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }


                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(UserProfileActivity.this);
                progressDialog.setTitle("Updating profile....");
                progressDialog.setMessage("Please wait");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String username = userName.getText().toString().trim();
                if (username.isEmpty()) {
                    userName.setError("Enter Name");
                    return;
                }
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

                            }
                        });
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();

                currentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    UserProfile userProfile = new UserProfile(token, currentUser.getUid(),
                                            username, currentUser.getPhoneNumber(), currentUser.getPhotoUrl().toString(),
                                            tagLine.getText().toString());
                                    myRef.child("Users").child("UserProfile").child(currentUser.getUid())
                                            .setValue(userProfile);
                                    progressDialog.dismiss();
                                    finish();
                                }
                            }
                        });
            }
        });


        userName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateName.setEnabled(true);
                userName.requestFocus();
                return true;
            }

        });
        tagLine.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateName.setEnabled(true);
                tagLine.requestFocus();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARY_PICK && resultCode == RESULT_OK && data != null) {
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
                final Uri resultUri = result.getUri();
                final ProgressDialog progressDialog = new ProgressDialog(UserProfileActivity.this);
                progressDialog.setTitle("Updating profile photo....");
                progressDialog.setMessage("Please wait");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                final StorageReference filePath = storageReference.child(currentUser.getUid() + ".jpg");
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(resultUri)
                        .build();
                currentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.d(TAG, "onSuccess: " + uri.toString());
                                                    myRef.child("Users").child("UserProfile").child(currentUser.getUid())
                                                            .child("imageUrl").setValue(uri.toString());
                                                    profile_image.setImageURI(resultUri);
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
