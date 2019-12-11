package com.beyondthehorizon.associates.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.beyondthehorizon.associates.R;

public class FriendProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    private TextView userTitle, userOnlineStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        toolbar = findViewById(R.id.ctsToolbar2);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
//        getSupportActionBar().setTitle(intent.getStringExtra("myFriendName"));
        userTitle = findViewById(R.id.userTitle);
        userOnlineStatus = findViewById(R.id.userOnlineStatus);
//        getSupportActionBar().setTitle("Kevo mweene");
//        getSupportActionBar().setSubtitle("Kevo");
        userTitle.setText("Kelvo Yule msee");
        userOnlineStatus.setText("last seen");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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
