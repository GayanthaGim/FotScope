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
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException; // Specific exception for wrong password
import com.google.firebase.auth.FirebaseAuthInvalidUserException;     // Specific exception for user not found
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private AppCompatButton btnSignIn;
    private TextView forgotPassword, textSignUp;
    private AppCompatImageButton btnGoogle, btnTwitter;

    private FirebaseAuth mAuth; // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        forgotPassword = findViewById(R.id.forgot_password);
        textSignUp = findViewById(R.id.text_sign_up);
        btnGoogle = findViewById(R.id.btn_google);
        btnTwitter = findViewById(R.id.btn_twitter);

        // Set click listeners
        btnSignIn.setOnClickListener(view -> signIn());
        forgotPassword.setOnClickListener(view -> handleForgotPassword());
        textSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Optional: Implement click listeners for Google/Twitter sign-in if needed
        // btnGoogle.setOnClickListener(view -> signInWithGoogle());
        // btnTwitter.setOnClickListener(view -> signInWithTwitter());
    }

    private void signIn() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Please enter email");
            inputEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Please enter password");
            inputPassword.requestFocus();
            return;
        }

        // Firebase sign-in logic
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(LoginActivity.this, "Welcome back, " + user.getEmail(), Toast.LENGTH_SHORT).show();

                            // Navigate to the main activity after successful login
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Ensure MainActivity.class is correct
                            startActivity(intent);
                            finish(); // Prevent back navigation to login screen
                        }
                    } else {
                        // If sign-in fails, display a more specific message based on Firebase exception
                        String errorMessage;
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            errorMessage = "Authentication Failed: No account found with this email.";
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            errorMessage = "Authentication Failed: Invalid password.";
                        } else if (task.getException() != null && task.getException().getMessage() != null) {
                            errorMessage = "Authentication Failed: " + task.getException().getMessage();
                        } else {
                            errorMessage = "Authentication Failed: An unknown error occurred.";
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("LoginActivity", "Authentication Error: " + errorMessage, task.getException());
                    }
                });
    }

    private void handleForgotPassword() {
        String email = inputEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Please enter your email to reset password.");
            inputEmail.requestFocus();
            return;
        }

        // Firebase password reset logic
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Password reset email sent to " + email, Toast.LENGTH_LONG).show();
                    } else {
                        String errorMessage = "Failed to send reset email. " + (task.getException() != null ? task.getException().getMessage() : "Unknown error.");
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("LoginActivity", "Password Reset Error: " + errorMessage, task.getException());
                    }
                });
    }
}