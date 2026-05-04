package com.g05.subtotal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;
import java.util.Random;

public class InsightsActivity extends AppCompatActivity {

    private SubscriptionViewModel viewModel;
    private TextView tvMoneyTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);

        // Link our XML views to Java
        TextView tvTotalSpend    = findViewById(R.id.tvTotalSpend);
        TextView tvSubCount      = findViewById(R.id.tvSubCount);
        TextView tvEntertainment = findViewById(R.id.tvEntertainment);
        TextView tvHealth        = findViewById(R.id.tvHealth);
        TextView tvEducation     = findViewById(R.id.tvEducation);
        tvMoneyTip               = findViewById(R.id.tvMoneyTip);

        // Initialize ViewModel. This handles our database connection.
        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        // Observe the database. Whenever a subscription changes, update the UI.
        viewModel.getAllSubscriptions().observe(this, subs -> {

            // If there's no data, reset everything to zero.
            if (subs == null || subs.isEmpty()) {
                tvTotalSpend.setText("$ 0");
                tvSubCount.setText("Across 0 Active Subscriptions");
                tvEntertainment.setText("$ 0");
                tvHealth.setText("$ 0");
                tvEducation.setText("$ 0");
                return;
            }

            // Variables to hold our running totals
            double total = 0, entertainment = 0, health = 0, education = 0;

            // Loop through every subscription in the database
            for (Subscription s : subs) {
                String billingCycle = s.getBillingCycle();
                double price = s.getPrice();

                // Calculate the yearly cost
                double yearly = billingCycle != null && billingCycle.equals("Yearly") ? price : price * 12;
                total += yearly;

                // Sort the spending into categories
                String cat = s.getCategory() != null ? s.getCategory() : "";
                switch (cat) {
                    case "Entertainment": entertainment += yearly; break;
                    case "Health":        health += yearly;        break;
                    case "Education":     education += yearly;     break;
                }
            }

            // Update the UI with the final calculated amounts formatted with a Dollar sign
            tvTotalSpend.setText(String.format(Locale.getDefault(), "$ %.0f", total));
            tvSubCount.setText("Across " + subs.size() + " Active Subscriptions");
            tvEntertainment.setText(String.format(Locale.getDefault(), "$ %.0f", entertainment));
            tvHealth.setText(String.format(Locale.getDefault(), "$ %.0f", health));
            tvEducation.setText(String.format(Locale.getDefault(), "$ %.0f", education));
        });

        // Show a random, highly relevant money saving tip
        setRandomMoneyTip();

        setupBottomNav();
    }

    private void setRandomMoneyTip() {
        String[] tips = {
                "Switch to annual plans to save an average of 15-20% on most subscriptions!",
                "Cancel subscriptions you haven't used in the last 30 days.",
                "Share family plans for music and video streaming to split costs with friends.",
                "Set calendar reminders 3 days before a free trial ends to avoid surprise charges.",
                "Rotate your streaming services! Binge one app for a month, then pause it and switch to another.",
                "Check if your student email gives you access to discounted software and streaming."
        };

        // Pick a random number between 0 and the length of our list
        Random random = new Random();
        int randomIndex = random.nextInt(tips.length);

        // Set the text on the screen
        tvMoneyTip.setText(tips[randomIndex]);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_insights);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_insights) {
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_timeline) {
                startActivity(new Intent(this, TimelineActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}