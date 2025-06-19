//package com.example.fotscope;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatButton;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class RegisterActivity extends AppCompatActivity {
//
//    private EditText inputEmail, inputPassword, inputConfirmPassword;
//    private AppCompatButton btnSignUp;
//    private TextView textSignIn;  // Navigate to Login Activity
//
//    private FirebaseAuth mAuth;  // Firebase Authentication instance
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register); // Your register activity layout
//
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
//        // Initialize views
//        inputEmail = findViewById(R.id.input_email);
//        inputPassword = findViewById(R.id.input_password);
//        inputConfirmPassword = findViewById(R.id.input_confirmpassword);
//
//        btnSignUp = findViewById(R.id.btn_sign_up);
//        textSignIn = findViewById(R.id.text_sign_in);
//
//        // Sign-Up button click
//        btnSignUp.setOnClickListener(view -> attemptSignUp());
//
//        // Navigate to LoginActivity when tapping "Sign In" text
//        textSignIn.setOnClickListener(view -> {
//            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        });
//    }
//
//    private void attemptSignUp() {
//        String email = inputEmail.getText().toString().trim();
//        String password = inputPassword.getText().toString();
//        String confirmPassword = inputConfirmPassword.getText().toString();
//
//        // Basic validation
//        if (TextUtils.isEmpty(email)) {
//            inputEmail.setError("Please enter your email");
//            inputEmail.requestFocus();
//            return;
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            inputEmail.setError("Please enter a valid email");
//            inputEmail.requestFocus();
//            return;
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            inputPassword.setError("Please enter a password");
//            inputPassword.requestFocus();
//            return;
//        }
//
//        if (password.length() < 6) {
//            inputPassword.setError("Password must be at least 6 characters");
//            inputPassword.requestFocus();
//            return;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            inputConfirmPassword.setError("Passwords do not match");
//            inputConfirmPassword.requestFocus();
//            return;
//        }
//
//        // Firebase sign-up with email and password
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        // Sign-up successful
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        if (user != null) {
//                            // Show a success message
//                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
//
//                            // Navigate to the main activity after successful registration
//                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();  // Close RegisterActivity
//                        }
//                    } else {
//                        // If sign-up fails, display a message to the user
//                        Toast.makeText(RegisterActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}
package com.example.fotscope;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.fotscope.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputConfirmPassword, inputFullName;
    private AppCompatButton btnSignUp;
    private TextView textSignIn;  // Navigate to Login Activity

    private FirebaseAuth mAuth;  // Firebase Authentication instance
    private FirebaseFirestore firestore; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Your register activity layout

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputConfirmPassword = findViewById(R.id.input_confirmpassword);
        inputFullName = findViewById(R.id.input_name); // New EditText for Full Name

        btnSignUp = findViewById(R.id.btn_sign_up);
        textSignIn = findViewById(R.id.text_sign_in);

        // Sign-Up button click
        btnSignUp.setOnClickListener(view -> attemptSignUp());

        // Navigate to LoginActivity when tapping "Sign In" text
        textSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptSignUp() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();
        String fullName = inputFullName.getText().toString().trim(); // Get full name

        // Basic validation
        if (TextUtils.isEmpty(fullName)) {
            inputFullName.setError("Please enter your full name");
            inputFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Please enter your email");
            inputEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Please enter a valid email");
            inputEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Please enter a password");
            inputPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            inputPassword.setError("Password must be at least 6 characters");
            inputPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            inputConfirmPassword.setError("Passwords do not match");
            inputConfirmPassword.requestFocus();
            return;
        }

        // Firebase sign-up with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-up successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save user data to Firestore
                            saveUserData(user, fullName, email);

                            // Show a success message
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            // Navigate to the main activity after successful registration
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();  // Close RegisterActivity
                        }
                    } else {
                        // If sign-up fails, display a message to the user
                        Toast.makeText(RegisterActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(FirebaseUser user, String fullName, String email) {
        // Create a new user object to save to Firestore
        User newUser = new User(fullName, email, user.getUid());  // Assuming User class has a constructor with these fields

        // Get a reference to the Firestore "users" collection
        DocumentReference userRef = firestore.collection("users").document(user.getUid());

        // Save the user data to Firestore
        userRef.set(newUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RegisterActivity", "User data successfully saved to Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("RegisterActivity", "Error saving user data", e);
                });
    }
}
