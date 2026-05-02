package com.g05.subtotal.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.g05.subtotal.R;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;

public class InsightsActivity extends AppCompatActivity {

    private SubscriptionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);

        TextView tvTotal = findViewById(R.id.tvTotalSpend);
        TextView tvEntertainment = findViewById(R.id.tvEntertainment);
        TextView tvHealth = findViewById(R.id.tvHealth);
        TextView tvCloud = findViewById(R.id.tvCloud);
        TextView tvOther = findViewById(R.id.tvOther);

        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        viewModel.totalMonthlySpend.observe(this, total -> {
            if (total != null) {
                tvTotal.setText("$" + String.format("%.2f", total));
            } else {
                tvTotal.setText("$0.00");
            }
        });

        viewModel.getSpendByCategory("Entertainment").observe(this, amount -> {
            tvEntertainment.setText("Entertainment: $" + (amount != null ? String.format("%.2f", amount) : "0.00"));
        });

        viewModel.getSpendByCategory("Health").observe(this, amount -> {
            tvHealth.setText("Health: $" + (amount != null ? String.format("%.2f", amount) : "0.00"));
        });

        viewModel.getSpendByCategory("Cloud").observe(this, amount -> {
            tvCloud.setText("Cloud: $" + (amount != null ? String.format("%.2f", amount) : "0.00"));
        });

        viewModel.getSpendByCategory("Other").observe(this, amount -> {
            tvOther.setText("Other: $" + (amount != null ? String.format("%.2f", amount) : "0.00"));
        });
    }
}