package com.g05.subtotal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.g05.subtotal.R;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;

/**
 * Full-screen delete confirmation screen.
 */
public class DeleteSubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_sub);

        // Retrieve data passed from SubDetailActivity
        int    id           = getIntent().getIntExtra(SubDetailActivity.EXTRA_ID, -1);
        String serviceName  = getIntent().getStringExtra(SubDetailActivity.EXTRA_SERVICE_NAME);

        // Set the dynamic title: "Remove Netflix?"
        TextView tvTitle = findViewById(R.id.tvDeleteTitle);
        if (serviceName != null && !serviceName.isEmpty()) {
            tvTitle.setText("Remove " + serviceName + "?");
        }

        SubscriptionViewModel viewModel =
                new ViewModelProvider(this).get(SubscriptionViewModel.class);

        // Yes, Remove — delete and go home
        Button btnConfirm = findViewById(R.id.btnConfirmDelete);
        btnConfirm.setOnClickListener(v -> {
            if (id == -1) {
                Toast.makeText(this, "Error: could not identify subscription", Toast.LENGTH_SHORT).show();
                return;
            }

            // FIXED: Use deleteById for maximum reliability. 
            // This avoids issues with object reconstruction.
            viewModel.deleteById(id);

            Toast.makeText(this,
                    (serviceName != null ? serviceName : "Subscription") + " removed",
                    Toast.LENGTH_SHORT).show();

            // Return to HomeActivity and clear stack
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Cancel — just go back
        Button btnCancel = findViewById(R.id.btnCancelDelete);
        btnCancel.setOnClickListener(v -> finish());
    }
}