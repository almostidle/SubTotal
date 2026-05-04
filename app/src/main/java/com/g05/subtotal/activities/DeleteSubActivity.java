package com.g05.subtotal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;

/**
 * Full-screen delete confirmation screen (S10 - Delete Sub).
 *
 * Started by SubDetailActivity when the user taps "Delete".
 * Receives the same extras as SubDetailActivity so it can reconstruct
 * the Subscription object and call viewModel.delete().
 *
 * On "Yes, Remove" → deletes from DB, navigates back to HomeActivity
 *   (clearing the back stack so SubDetailActivity is also popped).
 * On "Cancel"      → just finishes, returning to SubDetailActivity.
 */
public class DeleteSubActivity extends AppCompatActivity {

    // Re-use the same extra keys defined in SubDetailActivity
    public static final String EXTRA_ID             = SubDetailActivity.EXTRA_ID;
    public static final String EXTRA_SERVICE_NAME   = SubDetailActivity.EXTRA_SERVICE_NAME;
    public static final String EXTRA_PRICE          = SubDetailActivity.EXTRA_PRICE;
    public static final String EXTRA_BILLING_CYCLE  = SubDetailActivity.EXTRA_BILLING_CYCLE;
    public static final String EXTRA_CATEGORY       = SubDetailActivity.EXTRA_CATEGORY;
    public static final String EXTRA_NEXT_BILL_DATE = SubDetailActivity.EXTRA_NEXT_BILL_DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_sub);

        // Read subscription data passed from SubDetailActivity
        int    id           = getIntent().getIntExtra(EXTRA_ID, -1);
        String serviceName  = getIntent().getStringExtra(EXTRA_SERVICE_NAME);
        double price        = getIntent().getDoubleExtra(EXTRA_PRICE, 0.0);
        String billingCycle = getIntent().getStringExtra(EXTRA_BILLING_CYCLE);
        String category     = getIntent().getStringExtra(EXTRA_CATEGORY);
        String nextBillDate = getIntent().getStringExtra(EXTRA_NEXT_BILL_DATE);

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

            // Reconstruct the Subscription with its real DB id and delete it
            Subscription sub = new Subscription(
                    serviceName, price, billingCycle, category, nextBillDate);
            sub.id = id;
            viewModel.delete(sub);

            Toast.makeText(this,
                    (serviceName != null ? serviceName : "Subscription") + " removed",
                    Toast.LENGTH_SHORT).show();

            // Pop all the way back to HomeActivity
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Cancel — just go back to SubDetailActivity
        Button btnCancel = findViewById(R.id.btnCancelDelete);
        btnCancel.setOnClickListener(v -> finish());
    }
}