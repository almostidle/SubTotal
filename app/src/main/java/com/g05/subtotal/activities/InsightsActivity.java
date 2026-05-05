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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

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

        // Initialize ViewModel. This handles our database connection securely.
        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        // Observe the database. Whenever a subscription is added or deleted,
        // this block automatically runs and updates the UI.
        viewModel.getAllSubscriptions().observe(this, subs -> {

            // If there's no data, reset everything to zero.
            if (subs == null || subs.isEmpty()) {
                tvTotalSpend.setText("₹ 0");
                tvSubCount.setText("Across 0 Active Subscriptions");
                tvEntertainment.setText("₹ 0");
                tvHealth.setText("₹ 0");
                tvEducation.setText("₹ 0");
                return;
            }

            // Variables to hold our running totals
            double total = 0, entertainment = 0, health = 0, education = 0;

            // Loop through every subscription in the database
            for (Subscription s : subs) {
                String billingCycle = s.getBillingCycle();
                double price = s.getPrice();

                // Calculate the yearly cost. If it's already yearly, use the price.
                // If it's monthly, multiply by 12.
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
            tvTotalSpend.setText(String.format(Locale.getDefault(), "₹ %.0f", total));
            tvSubCount.setText("Across " + subs.size() + " Active Subscriptions");
            tvEntertainment.setText(String.format(Locale.getDefault(), "₹ %.0f", entertainment));
            tvHealth.setText(String.format(Locale.getDefault(), "₹ %.0f", health));
            tvEducation.setText(String.format(Locale.getDefault(), "₹ %.0f", education));
        });

        // Trigger the API call to get a random tip
        fetchMoneyTip();

        setupBottomNav();
    }

    // Method to explain in Viva: How to safely fetch internet data
    private void fetchMoneyTip() {
        // We open a new Thread so the network request doesn't freeze the main User Interface
        new Thread(() -> {
            String tip = null;
            try {
                // Connect to a free advice API
                URL url = new URL("https://api.adviceslip.com/advice");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000); // Wait max 5 seconds
                conn.setReadTimeout(5000);
                conn.setRequestProperty("Accept", "application/json");

                // Read the incoming data stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();
                conn.disconnect();

                // Convert the raw text into a JSON object and extract the "advice" string
                JSONObject json = new JSONObject(sb.toString());
                tip = json.getJSONObject("slip").getString("advice");
            } catch (Exception e) {
                // If the user has no internet, the exception fires and 'tip' stays null.
            }

            // Set a default tip if the API failed, otherwise use the fetched tip
            final String finalTip = tip != null
                    ? tip
                    : "Switch to annual plans to save an average of 15-20% on most subscriptions!";

            // We must go back to the Main (UI) Thread to change what the user sees on screen
            runOnUiThread(() -> tvMoneyTip.setText(finalTip));

        }).start();
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