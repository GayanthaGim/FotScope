package com.example.fotscope;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fotscope.fragments.AcademicFragment;
import com.example.fotscope.fragments.DevInfoFragment;
import com.example.fotscope.fragments.EventsFragment;
import com.example.fotscope.fragments.SportsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView profileIcon;  // Profile icon in the search bar
    private LinearLayout searchBarLayout; // Reference to the search bar layout

    private Fragment sportsFragment = new SportsFragment();
    private Fragment academicFragment = new AcademicFragment();
    private Fragment eventsFragment = new EventsFragment();
    private Fragment devInfoFragment = new DevInfoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        profileIcon = findViewById(R.id.profile_icon);
        searchBarLayout = findViewById(R.id.search_bar_layout); // Initialize the search bar layout

        // Set OnClickListener for profile icon
        profileIcon.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Check the selected fragment and load accordingly
            if (id == R.id.nav_sports) {
                loadFragment(sportsFragment);
                showSearchBar(true); // Show the search bar
                return true;
            } else if (id == R.id.nav_academic) {
                loadFragment(academicFragment);
                showSearchBar(true); // Show the search bar
                return true;
            } else if (id == R.id.nav_events) {
                loadFragment(eventsFragment);
                showSearchBar(true); // Show the search bar
                return true;
            } else if (id == R.id.nav_dev_info) {
                loadFragment(devInfoFragment);
                showSearchBar(false); // Hide the search bar for DevInfo fragment
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_sports); // Default to sports
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void showSearchBar(boolean show) {
        // Toggle the visibility of the search bar
        if (show) {
            searchBarLayout.setVisibility(View.VISIBLE);
        } else {
            searchBarLayout.setVisibility(View.GONE);
        }
    }
}
