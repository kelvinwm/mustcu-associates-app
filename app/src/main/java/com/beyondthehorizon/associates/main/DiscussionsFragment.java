package com.beyondthehorizon.associates.main;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.chats.AddNewPost;
import com.beyondthehorizon.associates.discussions.DiscussionSectionsAdapter;
import com.beyondthehorizon.associates.discussions.LatestFragment;
import com.beyondthehorizon.associates.discussions.TrendingFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscussionsFragment extends Fragment {

    private static final String TAG = "MAIN";

    public DiscussionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discussions, container, false);

        DiscussionSectionsAdapter sectionsPagerAdapter = new DiscussionSectionsAdapter(getActivity().getSupportFragmentManager());

        ViewPager viewPager = view.findViewById(R.id.view_pager2);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = view.findViewById(R.id.tabs2);
        tabs.setupWithViewPager(viewPager);
        sectionsPagerAdapter.addFragment(new LatestFragment(), "Latest Posts");
        sectionsPagerAdapter.addFragment(new TrendingFragment(), "Hot Topics");
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = view.findViewById(R.id.AddPostFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewPost addNewPost = new AddNewPost();
                addNewPost.show(getActivity().getSupportFragmentManager(), "Add New Post");
            }
        });
        return view;
    }

}
