package com.beyondthehorizon.testfirebasefunctionstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfile";
    Button updateName;
    EditText userName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.userName);
        updateName = findViewById(R.id.updateName);

        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                final String username = userName.getText().toString().trim();
                if (username.isEmpty()) {
                    userName.setError("Enter Name");
                    return;
                }

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                        .build();

                currentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    final DatabaseReference myRef = database.getReference();
                                    UserProfile userProfile = new UserProfile("", currentUser.getUid(), username);
                                    myRef.child("Users").child("UserProfile").child(currentUser.getUid())
                                            .setValue(userProfile);
                                    finish();
                                }
                            }
                        });
            }
        });
    }
}
