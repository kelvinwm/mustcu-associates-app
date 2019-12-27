package com.beyondthehorizon.associates;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.beyondthehorizon.associates.groupchat.AddGroupActivity;
import com.beyondthehorizon.associates.chats.AddNewPost;
import com.beyondthehorizon.associates.main.ChatsFragment;
import com.beyondthehorizon.associates.main.DiscussionsFragment;
import com.beyondthehorizon.associates.main.EventsFragment;
import com.beyondthehorizon.associates.main.SectionsPagerAdapter;
import com.beyondthehorizon.associates.users.UserProfileActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity implements AddNewPost.AddNewPostListener {
    private static final String TAG = "MAIN";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public static final String CHAT_PREFS = "UserChatInfo";
    public static final String MyFriendName = "myFriendName";
    public static final String FriendUID = "friendUID";
    public static final String ChatTypeFromChatsFragment = "chatTypeFromChatsFragment";
    public static final String ProfileUrlFromChatsFragment = "profileUrlFromChatsFragment";
    public static final String UserName = "userName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        sectionsPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        sectionsPagerAdapter.addFragment(new DiscussionsFragment(), "Discussions");
        sectionsPagerAdapter.addFragment(new EventsFragment(), "Events");
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
        userOnlineStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userOnlineStatus();
    }

    private void userOnlineStatus() {
        DatabaseReference userStatus =
                FirebaseDatabase.getInstance().getReference("Users/UserProfile/" + currentUser.getUid() + "/onlineStatus");
        userStatus.onDisconnect().setValue(ServerValue.TIMESTAMP);
        userStatus.setValue("online");
    }

    @Override
    public void addNewPostMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
            case R.id.menu_main_add_group:
                addGroup();
                return true;
            case R.id.menu_main_setting:
                viewProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewProfile() {
        startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
    }

    private void addGroup() {
        startActivity(new Intent(MainActivity.this, AddGroupActivity.class));
    }
}