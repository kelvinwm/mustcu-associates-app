package com.beyondthehorizon.associates.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.database.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    private TextView userTitle, userOnlineStatus, userName, tagLine, phoneNumber;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private CircleImageView profile_image;
    private RelativeLayout R2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        toolbar = findViewById(R.id.ctsToolbar2);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        Intent intent = getIntent();
        userTitle = findViewById(R.id.userTitle);
        userOnlineStatus = findViewById(R.id.userOnlineStatus);
        userName = findViewById(R.id.userName);
        tagLine = findViewById(R.id.tagLine);
        profile_image = findViewById(R.id.profile_image);
        phoneNumber = findViewById(R.id.phoneNumber);
        R2 = findViewById(R.id.R3);

        userTitle.setText(intent.getStringExtra("userName"));
        userName.setText(intent.getStringExtra("userName"));

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        /**GET USER ONLINE STATUS*/
        myRef.child("Users").child("UserProfile").child(intent.getStringExtra("userUid")).child("onlineStatus")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (!(dataSnapshot.getValue().toString().contains("online") ||
                                    dataSnapshot.getValue().toString().contains("Paused"))) {
                                Date date = new Date(Long.parseLong(dataSnapshot.getValue().toString()));
                                SimpleDateFormat sfd = new SimpleDateFormat("HH:mm a  dd-MM-yyyy");
                                userOnlineStatus.setText("last seen " + sfd.format(date));
                                return;
                            }
                            userOnlineStatus.setText(dataSnapshot.getValue().toString());
                        } else {
                            userOnlineStatus.setText("Offline");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        /**GET USER PROFILE STATUS*/
        myRef.child("Users").child("UserProfile").child(intent.getStringExtra("userUid"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            userName.setText(dataSnapshot.child("userName").getValue().toString());
                            tagLine.setText(dataSnapshot.child("tagLine").getValue().toString());
                            phoneNumber.setText(dataSnapshot.child("phoneNumber").getValue().toString());

                            Picasso.get().load(dataSnapshot.child("imageUrl").getValue().toString()).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    R2.setBackground(new BitmapDrawable(bitmap));
                                    Picasso.get().load(dataSnapshot.child("imageUrl").getValue().toString())
                                            .fit().placeholder(R.drawable.loader)
                                            .error(R.drawable.ic_broken_image)
                                            .into(profile_image);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity
        }
        return super.onOptionsItemSelected(item);
    }
}
