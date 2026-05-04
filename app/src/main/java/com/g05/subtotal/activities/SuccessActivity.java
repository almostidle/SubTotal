package com.g05.subtotal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.g05.subtotal.R;

import java.util.Locale;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        String service      = getIntent().getStringExtra("service");
        double price        = getIntent().getDoubleExtra("price", 0.0);
        String billingCycle = getIntent().getStringExtra("billingCycle");
        String category     = getIntent().getStringExtra("category");
        String nextBillDate = getIntent().getStringExtra("nextBillDate");

        TextView tvService  = findViewById(R.id.tvSuccessService);
        TextView tvPrice    = findViewById(R.id.tvSuccessPrice);
        TextView tvCategory = findViewById(R.id.tvSuccessCategory);
        TextView tvDate     = findViewById(R.id.tvSuccessDate);

        if (service != null)      tvService.setText(service);
        if (billingCycle != null) {
            tvPrice.setText(String.format(Locale.getDefault(),
                    "₹%.2f/%s", price, "Monthly".equals(billingCycle) ? "mo" : "yr"));
        }
        if (category != null)     tvCategory.setText(category);
        if (nextBillDate != null) tvDate.setText(nextBillDate);

        findViewById(R.id.btnContinue).setOnClickListener(v -> {
            Intent intent = new Intent(this, TimelineActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}