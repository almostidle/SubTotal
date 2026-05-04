package com.g05.subtotal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.g05.subtotal.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirm;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirm = findViewById(R.id.et_confirm_password);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_create).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (TextUtils.isEmpty(name)) { etName.setError("Name is required"); return; }
            if (TextUtils.isEmpty(email)) { etEmail.setError("Email is required"); return; }
            if (password.length() < 6) { etPassword.setError("Password must be at least 6 characters"); return; }
            if (!password.equals(confirm)) { etConfirm.setError("Passwords do not match"); return; }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, SignInActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Sign up failed: " +
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}