package com.g05.subtotal.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsightsActivity extends AppCompatActivity {

    private SubscriptionViewModel viewModel;
    private TextView tvTotalYearlyAmount, tvActiveCount;
    private LinearLayout llCategoryBreakdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);

        tvTotalYearlyAmount = findViewById(R.id.tvTotalYearlyAmount);
        tvActiveCount = findViewById(R.id.tvActiveCount);
        llCategoryBreakdown = findViewById(R.id.llCategoryBreakdown);

        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        viewModel.allSubscriptions.observe(this, this::updateUI);
    }

    private void updateUI(List<Subscription> subscriptions) {
        if (subscriptions == null) return;

        double totalYearly = 0;
        int activeCount = subscriptions.size();
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Subscription sub : subscriptions) {
            double yearlyPrice = sub.billingCycle.equalsIgnoreCase("Monthly") ? sub.price * 12 : sub.price;
            totalYearly += yearlyPrice;

            String cat = sub.category != null ? sub.category : "Other";
            categoryTotals.put(cat, categoryTotals.getOrDefault(cat, 0.0) + yearlyPrice);
        }

        tvTotalYearlyAmount.setText("$ " + (int) totalYearly);
        tvActiveCount.setText("Across " + activeCount + " Active Subscriptions");

        // Category Breakdown Rows
        llCategoryBreakdown.removeAllViews();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            addCategoryRow(entry.getKey(), entry.getValue());
        }
    }

    private void addCategoryRow(String name, double amount) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 8);
        
        TextView tvName = new TextView(this);
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        tvName.setText(name);
        tvName.setTextColor(0xFF1A1A1A);
        
        TextView tvAmount = new TextView(this);
        tvAmount.setText("$ " + (int) amount);
        tvAmount.setTextColor(0xFF1A1A1A);
        tvAmount.setTypeface(null, android.graphics.Typeface.BOLD);
        
        row.addView(tvName);
        row.addView(tvAmount);
        llCategoryBreakdown.addView(row);
    }
}
