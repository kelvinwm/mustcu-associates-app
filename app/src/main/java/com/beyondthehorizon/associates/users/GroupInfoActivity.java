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
import android.widget.ImageView;
import android.widget.TextView;

import com.beyondthehorizon.associates.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class GroupInfoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView userTitle, numberOfUsers;
    private ImageView group_image;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        toolbar = findViewById(R.id.ctsToolbar3);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        Intent intent = getIntent();
        userTitle = findViewById(R.id.userTitle2);
        numberOfUsers = findViewById(R.id.numberOfUsers);
        group_image = findViewById(R.id.group_image);
        userTitle.setText(intent.getStringExtra("groupName"));
        Log.d("GROUPNAME", "onCreate: " + intent.getStringExtra("groupName"));
        Picasso.get().load(intent.getStringExtra("groupImage"))
                .fit().placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken_image)
                .into(group_image);
        /**GET USER PROFILE STATUS*/
        myRef.child("Rooms").child(intent.getStringExtra("groupUid")).child(intent.getStringExtra("groupName"))
                .child("GroupInfo")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            numberOfUsers.setText(dataSnapshot.child("numberOfMembers").getValue().toString() + " participants");
                            Picasso.get().load(dataSnapshot.child("imageUrl").getValue().toString())
                                    .fit().placeholder(R.drawable.ic_image)
                                    .error(R.drawable.ic_broken_image)
                                    .into(group_image);
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
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
