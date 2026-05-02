package com.g05.subtotal.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.g05.subtotal.R;

public class SignInActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private CheckBox cbRemember;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        prefs = getSharedPreferences("subtotal_prefs", MODE_PRIVATE);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);

        if (prefs.getBoolean("remember_me", false)) {
            etEmail.setText(prefs.getString("email", ""));
            cbRemember.setChecked(true);
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_signin).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password is required");
                return;
            }

            SharedPreferences.Editor editor = prefs.edit();
            if (cbRemember.isChecked()) {
                editor.putBoolean("remember_me", true);
                editor.putString("email", email);
            } else {
                editor.clear();
            }
            editor.apply();

            Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.tv_forgot).setOnClickListener(v -> {
            Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_google).setOnClickListener(v -> {
            Toast.makeText(this, "Google Sign-In coming soon", Toast.LENGTH_SHORT).show();
        });
    }
}
