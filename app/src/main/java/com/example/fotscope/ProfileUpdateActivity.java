package com.example.fotscope;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType; // For setting input type of EditText in dialog
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // Still needed for dialogs
import android.widget.ImageView;
import android.widget.TextView; // Now used for displaying fields
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // For popup dialogs
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileUpdateActivity extends AppCompatActivity {

    private ImageView profileImage;
    private Button editPhotoButton;
    private TextView fullNameTextView, emailTextView, phoneNumberTextView; // Changed to TextView
    private Button updateBtn, saveBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String currentUserId;

    private static final int PICK_IMAGE_REQUEST = 1;

    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile_activity);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
            Log.d("ProfileUpdate", "User is logged in: " + currentUserId);
        } else {
            Log.e("ProfileUpdate", "User is not authenticated! Redirecting to login.");
            Intent loginIntent = new Intent(ProfileUpdateActivity.this, LoginActivity.class); // Replace LoginActivity.class
            startActivity(loginIntent);
            finish();
            return;
        }

        profileImage = findViewById(R.id.profile_image);
        editPhotoButton = findViewById(R.id.edit_photo_button);
        fullNameTextView = findViewById(R.id.full_name_display); // Use new ID for TextView
        emailTextView = findViewById(R.id.email_display);     // Use new ID for TextView
        phoneNumberTextView = findViewById(R.id.phone_number_display); // Use new ID for TextView
        updateBtn = findViewById(R.id.updatebtn);
        saveBtn = findViewById(R.id.savebtn);

        // Email field is always non-editable and not clickable
        emailTextView.setClickable(false);
        emailTextView.setFocusable(false);


        fetchUserData(); // Grab details from database and show them
        setEditMode(false); // Start in view mode

        // Set up click listeners
        editPhotoButton.setOnClickListener(v -> openImagePicker());

        updateBtn.setOnClickListener(v -> {
            if (!isEditMode) {
                setEditMode(true); // Enter edit mode
            } else {
                // This is the "Cancel" action
                fetchUserData(); // Revert any changes by re-fetching original data
                setEditMode(false); // Exit edit mode
                Toast.makeText(this, "Editing cancelled.", Toast.LENGTH_SHORT).show();
            }
        });

        saveBtn.setOnClickListener(v -> updateProfile()); // Call updateProfile to save changes

        // Set up listeners for TextViews to open edit dialogs when in edit mode
        fullNameTextView.setOnClickListener(v -> {
            if (isEditMode) {
                showEditDialog("Full Name", fullNameTextView.getText().toString(), fullNameTextView, InputType.TYPE_CLASS_TEXT);
            }
        });

        phoneNumberTextView.setOnClickListener(v -> {
            if (isEditMode) {
                showEditDialog("Phone Number", phoneNumberTextView.getText().toString(), phoneNumberTextView, InputType.TYPE_CLASS_PHONE);
            }
        });
    }

    /**
     * Sets the UI elements based on the edit mode.
     * @param enableEdit true to enable editing, false to disable.
     */
    private void setEditMode(boolean enableEdit) {
        isEditMode = enableEdit;

        // Make Full Name and Phone Number TextViews clickable/non-clickable
        fullNameTextView.setClickable(enableEdit);
        fullNameTextView.setFocusable(enableEdit); // So it shows ripple effect on tap

        phoneNumberTextView.setClickable(enableEdit);
        phoneNumberTextView.setFocusable(enableEdit); // So it shows ripple effect on tap


        // Toggle visibility of Save button
        saveBtn.setVisibility(enableEdit ? View.VISIBLE : View.GONE);

        // Toggle visibility of Edit Photo button
        editPhotoButton.setVisibility(enableEdit ? View.VISIBLE : View.GONE);

        // Change text of the primary button (updateBtn)
        updateBtn.setText(enableEdit ? "Cancel" : "Edit Profile");
    }

    /**
     * Fetches the current user's profile data from Firestore and populates the fields.
     */
    private void fetchUserData() {
        Log.d("ProfileUpdate", "Fetching user data from Firestore...");

        firestore.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String email = documentSnapshot.getString("email");
                        String phoneNumber = documentSnapshot.getString("phoneNumber");
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                        Log.d("ProfileUpdate", "Fetched user data: " + fullName + ", " + email + ", " + phoneNumber);

                        fullNameTextView.setText(fullName != null && !fullName.isEmpty() ? fullName : "Tap to edit full name");
                        emailTextView.setText(email != null && !email.isEmpty() ? email : "Email not available");
                        phoneNumberTextView.setText(phoneNumber != null && !phoneNumber.isEmpty() ? phoneNumber : "Tap to edit phone number");

                        // Apply CircleCrop transformation
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(ProfileUpdateActivity.this)
                                    .load(profileImageUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .placeholder(R.drawable.profile_picture)
                                    .error(R.drawable.profile_picture)
                                    .into(profileImage);
                        } else {
                            Glide.with(ProfileUpdateActivity.this)
                                    .load(R.drawable.profile_picture)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profileImage);
                        }
                    } else {
                        Log.e("ProfileUpdate", "User document does not exist for ID: " + currentUserId);
                        Toast.makeText(ProfileUpdateActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileUpdate", "Failed to fetch data from Firestore", e);
                    Toast.makeText(ProfileUpdateActivity.this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Shows an AlertDialog for editing a specific profile field.
     * @param title The title of the dialog (e.g., "Edit Full Name").
     * @param currentValue The current value to pre-fill the EditText.
     * @param targetTextView The TextView on the main activity to update.
     * @param inputType The input type for the EditText (e.g., InputType.TYPE_CLASS_TEXT).
     */
    private void showEditDialog(String title, String currentValue, final TextView targetTextView, int inputType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(inputType);
        input.setText(currentValue);
        input.setPadding(30, 30, 30, 30); // Add some padding
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newValue = input.getText().toString().trim();
                if (newValue.isEmpty()) {
                    Toast.makeText(ProfileUpdateActivity.this, title + " cannot be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    targetTextView.setText(newValue); // Update the TextView on the main activity
                    Toast.makeText(ProfileUpdateActivity.this, title + " updated locally.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Updates the user's full name and phone number in Firestore based on the TextViews' content.
     */
    private void updateProfile() {
        String fullName = fullNameTextView.getText().toString().trim();
        String phoneNumber = phoneNumberTextView.getText().toString().trim();

        // Basic validation against default hints/empty values if the user cleared them
        if (fullName.isEmpty() || fullName.equals("Tap to edit full name")) {
            Toast.makeText(this, "Full Name cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phoneNumber.isEmpty() || phoneNumber.equals("Tap to edit phone number")) {
            Toast.makeText(this, "Phone Number cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ProfileUpdate", "Updating profile with: " + fullName + ", " + phoneNumber);

        firestore.collection("users")
                .document(currentUserId)
                .update("fullName", fullName, "phoneNumber", phoneNumber)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ProfileUpdate", "Profile text data updated successfully");
                    Toast.makeText(ProfileUpdateActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    fetchUserData(); // Refresh displayed data after update (re-fetches from DB)
                    setEditMode(false); // Exit edit mode after successful save
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileUpdate", "Failed to update profile text data", e);
                    Toast.makeText(ProfileUpdateActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
            uploadImageToFirebaseStorage(imageUri);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (currentUserId == null) {
            Toast.makeText(this, "User not logged in. Cannot upload image.", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileImageRef = storageRef.child("profile_images/" + currentUserId + "/profile.jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        Log.d("ProfileUpdate", "Image uploaded. Download URL: " + downloadUrl);
                        updateProfileImageUrlInFirestore(downloadUrl);
                    }).addOnFailureListener(e -> {
                        Log.e("ProfileUpdate", "Failed to get download URL", e);
                        Toast.makeText(ProfileUpdateActivity.this, "Failed to get image download URL", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileUpdate", "Failed to upload image", e);
                    Toast.makeText(ProfileUpdateActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateProfileImageUrlInFirestore(String imageUrl) {
        firestore.collection("users")
                .document(currentUserId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ProfileUpdate", "Profile image URL updated successfully in Firestore");
                    Toast.makeText(ProfileUpdateActivity.this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show();
                    fetchUserData();
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileUpdate", "Failed to update profile image URL in Firestore", e);
                    Toast.makeText(ProfileUpdateActivity.this, "Failed to save profile picture URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}