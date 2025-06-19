package com.example.fotscope;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ArticleDetailActivity extends AppCompatActivity {

    private static final String TAG = "ArticleDetailActivity"; // Consistent TAG for logging

    private TextView detailTitle, detailDate, detailContent;
    private ImageView detailImage;

    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail); // Use your layout file

        // Crucial: Early check if the activity is finishing or destroyed
        // This prevents attempting UI operations on a dead activity instance.
        if (isFinishing() || isDestroyed()) {
            Log.d(TAG, "Activity is finishing or destroyed during onCreate. Aborting initialization.");
            return;
        }

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();

        // Initialize views
        detailTitle = findViewById(R.id.detailTitle);
        detailDate = findViewById(R.id.detailDate);
        detailContent = findViewById(R.id.detailContent);
        detailImage = findViewById(R.id.detailImage);

        // Retrieve data passed from the adapter/previous activity via Intent
        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        String content = getIntent().getStringExtra("fullSummary");
        String imageUrl = getIntent().getStringExtra("imageUrl"); // Assumed to be a gs:// URL if from Firebase Storage

        // Set the data to the views, with null/empty checks for robustness
        detailTitle.setText(title != null && !title.isEmpty() ? title : "No Title Available");
        detailDate.setText(date != null && !date.isEmpty() ? date : "No Date Available");
        detailContent.setText(content != null && !content.isEmpty() ? content : "Article content is not available.");

        // Load the image if a URL is provided
        if (imageUrl != null && !imageUrl.isEmpty()) {
            loadImage(imageUrl);
        } else {
            // If no image URL, set a local placeholder image
            // Ensure activity is still valid before performing UI operation
            if (!isFinishing() && !isDestroyed()) {
                detailImage.setImageResource(R.drawable.placeholder_image);
                Log.d(TAG, "No image URL provided. Setting default placeholder image.");
            }
        }
    }

    /**
     * Loads an image from Firebase Storage into the ImageView.
     * Includes activity lifecycle checks and Glide's placeholder/error handling.
     * @param imageUrl The Firebase Storage URL (e.g., "gs://your-bucket-name/images/myimage.jpg").
     */
    private void loadImage(String imageUrl) {
        // Crucial: Check activity state before starting the asynchronous image load
        if (isFinishing() || isDestroyed()) {
            Log.d(TAG, "Activity is finishing or destroyed. Skipping image load for URL: " + imageUrl);
            return;
        }

        // Create a Firebase Storage reference from the gs:// URL
        StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);

        // Get the public download URL for the image
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Crucial: Check activity state again after successfully getting the download URL
            if (isFinishing() || isDestroyed()) {
                Log.d(TAG, "Activity is finishing or destroyed after getting download URL. Skipping Glide load.");
                return;
            }

            // Use Glide to load the image into the ImageView
            Glide.with(ArticleDetailActivity.this) // Use activity context
                    .load(uri) // Load the actual download URL
                    .placeholder(R.drawable.circle_background) // Optional: Show a placeholder image while loading
                    .error(R.drawable.ic_search) // Optional: Show an error image if loading fails
                    .into(detailImage);
            Log.d(TAG, "Image loaded successfully from Firebase Storage. Download URL: " + uri.toString());
        }).addOnFailureListener(e -> {
            // Crucial: Check activity state after the image download fails
            if (isFinishing() || isDestroyed()) {
                Log.d(TAG, "Activity is finishing or destroyed after image load failure. Skipping error UI update.");
                return;
            }

            // Handle failure: set a default local image and notify the user
            Glide.with(ArticleDetailActivity.this)
                    .load(R.drawable.placeholder_image) // Fallback to a local default image
                    .into(detailImage);
            String errorMessage = "Error loading image: " + (e != null ? e.getMessage() : "Unknown error");
            Toast.makeText(ArticleDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to load image from Firebase Storage for URL: " + imageUrl, e);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // You can re-fetch data here if it might change while this activity is in the background
        // For ArticleDetailActivity, typically data is static once loaded, so not always needed.
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Release resources or stop ongoing operations if necessary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any listeners or ongoing tasks that might cause memory leaks
    }
}