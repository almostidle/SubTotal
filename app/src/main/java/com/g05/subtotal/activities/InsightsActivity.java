package com.g05.subtotal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;

import java.util.List;
import java.util.Locale;

public class InsightsActivity extends AppCompatActivity {

    private SubscriptionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);

        TextView tvTotalSpend   = findViewById(R.id.tvTotalSpend);
        TextView tvSubCount     = findViewById(R.id.tvSubCount);
        TextView tvEntertainment = findViewById(R.id.tvEntertainment);
        TextView tvHealth       = findViewById(R.id.tvHealth);
        TextView tvEducation    = findViewById(R.id.tvEducation);

        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        viewModel.allSubscriptions.observe(this, subs -> {
            if (subs == null || subs.isEmpty()) {
                tvTotalSpend.setText("$ 0");
                tvSubCount.setText("Across 0 Active Subscriptions");
                tvEntertainment.setText("$ 0");
                tvHealth.setText("$ 0");
                tvEducation.setText("$ 0");
                return;
            }

            double total = 0, entertainment = 0, health = 0, education = 0;

            for (Subscription s : subs) {
                double yearly = s.billingCycle != null && s.billingCycle.equals("Yearly")
                        ? s.price : s.price * 12;
                total += yearly;

                String cat = s.category != null ? s.category : "";
                switch (cat) {
                    case "Entertainment": entertainment += yearly; break;
                    case "Health":        health += yearly; break;
                    case "Education":     education += yearly; break;
                }
            }

            tvTotalSpend.setText(String.format(Locale.getDefault(), "$ %.0f", total));
            tvSubCount.setText("Across " + subs.size() + " Active Subscriptions");
            tvEntertainment.setText(String.format(Locale.getDefault(), "$ %.0f", entertainment));
            tvHealth.setText(String.format(Locale.getDefault(), "$ %.0f", health));
            tvEducation.setText(String.format(Locale.getDefault(), "$ %.0f", education));
        });

        // Bottom nav
        ImageButton btnHome     = findViewById(R.id.btnNavHome);
        ImageButton btnInsights = findViewById(R.id.btnNavInsights);
        ImageButton btnTimeline = findViewById(R.id.btnNavTimeline);

        btnHome.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        btnInsights.setOnClickListener(v -> { /* already here */ });
        btnTimeline.setOnClickListener(v -> startActivity(new Intent(this, TimelineActivity.class)));
    }
}