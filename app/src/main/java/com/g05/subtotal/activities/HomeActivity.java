package com.g05.subtotal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private SubscriptionViewModel subscriptionViewModel;
    private SubscriptionAdapter subscriptionAdapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyStateLayout;
    private TextView tvTotalMonthly;
    private FloatingActionButton fabAdd;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupBottomNav();
        setupFab();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewSubscriptions);
        emptyStateLayout = findViewById(R.id.layoutEmptyState);
        tvTotalMonthly = findViewById(R.id.tvTotalMonthly);
        fabAdd = findViewById(R.id.fabAdd);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        subscriptionAdapter = new SubscriptionAdapter(subscription -> {
            // FIXED: Use the correct keys defined in SubDetailActivity and pass all required data
            Intent intent = new Intent(HomeActivity.this, SubDetailActivity.class);
            intent.putExtra(SubDetailActivity.EXTRA_ID,             subscription.getId());
            intent.putExtra(SubDetailActivity.EXTRA_SERVICE_NAME,   subscription.getServiceName());
            intent.putExtra(SubDetailActivity.EXTRA_PRICE,          subscription.getPrice());
            intent.putExtra(SubDetailActivity.EXTRA_BILLING_CYCLE,  subscription.getBillingCycle());
            intent.putExtra(SubDetailActivity.EXTRA_CATEGORY,       subscription.getCategory());
            intent.putExtra(SubDetailActivity.EXTRA_NEXT_BILL_DATE, subscription.getNextBillDate());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(subscriptionAdapter);
    }

    private void setupViewModel() {
        subscriptionViewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        subscriptionViewModel.getAllSubscriptions().observe(this, subscriptions -> {
            subscriptionAdapter.setSubscriptions(subscriptions);
            updateEmptyState(subscriptions);
            updateTotalCost(subscriptions);
        });
    }

    private void updateEmptyState(List<Subscription> subscriptions) {
        if (subscriptions == null || subscriptions.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    private void updateTotalCost(List<Subscription> subscriptions) {
        if (subscriptions == null) return;
        double total = 0;
        for (Subscription s : subscriptions) {
            double cost = s.getPrice();
            String cycle = s.getBillingCycle();
            if (cycle != null) {
                switch (cycle.toLowerCase()) {
                    case "yearly":  cost = cost / 12; break;
                    case "weekly":  cost = cost * 4;  break;
                    case "daily":   cost = cost * 30; break;
                }
            }
            total += cost;
        }
        tvTotalMonthly.setText(String.format("₹%.2f / month", total));
    }

    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
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
            } else if (id == R.id.nav_insights) {
                startActivity(new Intent(this, InsightsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddSubscriptionActivity.class));
        });
    }
}
