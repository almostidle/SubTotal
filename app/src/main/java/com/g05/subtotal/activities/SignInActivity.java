package com.g05.subtotal.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.g05.subtotal.R;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private EditText etEmail, etPassword;
    private CheckBox cbRemember;
    private SharedPreferences prefs;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                try {
                    GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                            .getResult(ApiException.class);
                    if (account != null) {
                        firebaseAuthWithGoogle(account.getIdToken());
                    }
                } catch (ApiException e) {
                    // Status code 10 often means SHA-1 is missing in Firebase Console
                    String errorMsg = "Google Sign-In failed (Status: " + e.getStatusCode() + ")";
                    Log.e(TAG, errorMsg, e);
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("subtotal_prefs", MODE_PRIVATE);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);

        if (prefs.getBoolean("remember_me", false)) {
            etEmail.setText(prefs.getString("email", ""));
            cbRemember.setChecked(true);
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Email/Password Login
        findViewById(R.id.btn_signin).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) { etEmail.setError("Email is required"); return; }
            if (TextUtils.isEmpty(password)) { etPassword.setError("Password is required"); return; }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    handleLoginSuccess(email);
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(this, "Sign in failed: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });

        // Forgot Password
        findViewById(R.id.tv_forgot).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter your email first");
                return;
            }
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Reset link sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Google Sign-In Click
        findViewById(R.id.btn_google).setOnClickListener(v -> {
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            });
        });
    }

    private void handleLoginSuccess(String email) {
        SharedPreferences.Editor editor = prefs.edit();
        if (cbRemember.isChecked()) {
            editor.putBoolean("remember_me", true);
            editor.putString("email", email);
        } else {
            editor.clear();
        }
        editor.apply();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                Toast.makeText(this, "Firebase Google Auth failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}