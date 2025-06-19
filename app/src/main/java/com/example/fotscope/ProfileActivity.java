package com.example.fotscope;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView; // Added for profile image
import android.widget.TextView; // Added for username TextView
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions; // Required for circleCropTransform
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore; // Required for Firestore

public class ProfileActivity extends AppCompatActivity {

    private Button signOutButton, accountButton, privacyButton, notificationButton, supportButton, aboutUsButton; // All buttons
    private ImageView profileImage; // Profile image view
    private TextView usernameTextView; // Username text view

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore; // Firestore instance
    private String currentUserId; // Current user's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Check if user is authenticated
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            Log.d("ProfileActivity", "User is logged in: " + currentUserId);
        } else {
            Log.e("ProfileActivity", "User is not authenticated! Redirecting to login.");
            Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class); // Ensure LoginActivity.class is correct
            startActivity(loginIntent);
            finish();
            return;
        }

        // Initialize Views
        profileImage = findViewById(R.id.profileImage);
        usernameTextView = findViewById(R.id.username);
        signOutButton = findViewById(R.id.signOutButton);
        accountButton = findViewById(R.id.accountButton);
        privacyButton = findViewById(R.id.privacyButton);
        notificationButton = findViewById(R.id.notificationButton);
        supportButton = findViewById(R.id.supportButton);
        aboutUsButton = findViewById(R.id.aboutUsButton);

        // Fetch and display user data (username and profile picture)
        fetchUserProfileData();

        // Set OnClickListeners
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignOutDialog();
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToProfileUpdate();
            }
        });

        // Add listeners for other buttons if they have functionality
        privacyButton.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Privacy Settings Clicked", Toast.LENGTH_SHORT).show());
        notificationButton.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Notification Settings Clicked", Toast.LENGTH_SHORT).show());
        supportButton.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Support & Services Clicked", Toast.LENGTH_SHORT).show());
        aboutUsButton.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "About Us Clicked", Toast.LENGTH_SHORT).show());
    }

    /**
     * Fetches user's full name and profile image URL from Firestore.
     */
    private void fetchUserProfileData() {
        if (currentUserId == null) {
            Log.e("ProfileActivity", "Current user ID is null. Cannot fetch profile data.");
            return;
        }

        firestore.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        // Set username
                        if (fullName != null && !fullName.isEmpty()) {
                            usernameTextView.setText(fullName);
                        } else {
                            usernameTextView.setText("No Name"); // Default if full name is missing
                        }

                        // Load profile image with circular transformation
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(ProfileActivity.this)
                                    .load(profileImageUrl)
                                    .apply(RequestOptions.circleCropTransform()) // Make image round
                                    .placeholder(R.drawable.profile_picture) // Placeholder while loading
                                    .error(R.drawable.profile_picture)     // Error image if loading fails
                                    .into(profileImage);
                        } else {
                            // Load default local drawable and apply circle crop
                            Glide.with(ProfileActivity.this)
                                    .load(R.drawable.profile_picture)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profileImage);
                        }
                    } else {
                        Log.e("ProfileActivity", "User document not found in Firestore for ID: " + currentUserId);
                        Toast.makeText(ProfileActivity.this, "Profile data not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileActivity", "Error fetching profile data: " + e.getMessage(), e);
                    Toast.makeText(ProfileActivity.this, "Failed to load profile data.", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to show the Sign Out confirmation dialog
    private void showSignOutDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to sign out?")
                .setCancelable(true) // User can dismiss if they change their mind
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        signOut();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Method to sign out the user
    private void signOut() {
        mAuth.signOut();
        Toast.makeText(this, "Signed out successfully.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class); // Change to your actual login activity
        startActivity(intent);
        finish();
    }

    // Method to navigate to ProfileUpdateActivity when Account button is clicked
    private void navigateToProfileUpdate() {
        Intent intent = new Intent(ProfileActivity.this, ProfileUpdateActivity.class);
        startActivity(intent);
    }
}