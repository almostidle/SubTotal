package com.g05.subtotal.activities;

/*
 * BUG FIX #5 continued — HomeActivity (Java side):
 *
 * Issue A — Wrong package throughout:
 *   Every import said "com.example.subtotal.*". The project package is
 *   "com.g05.subtotal". This means HomeActivity imported classes that don't
 *   exist under that path — a compile error that prevents the entire app
 *   from building, or a ClassNotFoundException at runtime depending on
 *   how the build is configured.
 *
 * Issue B — Calling getAllSubscriptions() as a method:
 *   HomeActivity called subscriptionViewModel.getAllSubscriptions() but
 *   SubscriptionViewModel exposes this as a public field (allSubscriptions),
 *   not a method. Calling a non-existent method = compile error.
 *
 * Issue C — Calling subscription.getCost() / getId() / getBillingCycle() etc:
 *   The Subscription model has plain public fields (price, id, billingCycle),
 *   not JavaBean getters. These calls would fail at compile time.
 *
 * Fix: corrected all package names, use the public LiveData field directly,
 *   use direct field access on Subscription, and import the correct adapter.
 */

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
import com.g05.subtotal.adapters.SubscriptionAdapter;
import com.g05.subtotal.model.Subscription;              // FIXED: was com.example.subtotal
import com.g05.subtotal.viewmodel.SubscriptionViewModel; // FIXED: was com.example.subtotal
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private SubscriptionViewModel subscriptionViewModel;
    private SubscriptionAdapter   subscriptionAdapter;
    private RecyclerView          recyclerView;
    private LinearLayout          emptyStateLayout;
    private TextView              tvTotalMonthly;
    private FloatingActionButton  fabAdd;
    private BottomNavigationView  bottomNavigationView;

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
        recyclerView         = findViewById(R.id.recyclerViewSubscriptions);
        emptyStateLayout     = findViewById(R.id.layoutEmptyState);
        tvTotalMonthly       = findViewById(R.id.tvTotalMonthly);
        fabAdd               = findViewById(R.id.fabAdd);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        // FIXED: SubscriptionAdapter now has no-arg constructor + setSubscriptions()
        subscriptionAdapter = new SubscriptionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(subscriptionAdapter);
    }

    private void setupViewModel() {
        subscriptionViewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        // FIXED: observe the public LiveData *field* directly, not a non-existent method
        subscriptionViewModel.allSubscriptions.observe(this, subscriptions -> {
            subscriptionAdapter.setSubscriptions(subscriptions);
            updateEmptyState(subscriptions);
            updateTotalCost(subscriptions);
        });
    }

    private void updateEmptyState(List<Subscription> subscriptions) {
        boolean empty = subscriptions == null || subscriptions.isEmpty();
        recyclerView.setVisibility(empty ? View.GONE  : View.VISIBLE);
        emptyStateLayout.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void updateTotalCost(List<Subscription> subscriptions) {
        if (subscriptions == null) return;
        double total = 0;
        for (Subscription s : subscriptions) {
            // FIXED: use direct field access — getCost() / getBillingCycle() don't exist
            double cost  = s.price;
            String cycle = s.billingCycle;
            if (cycle != null) {
                switch (cycle.toLowerCase()) {
                    case "yearly": cost = cost / 12; break;
                    case "weekly": cost = cost * 4;  break;
                    case "daily":  cost = cost * 30; break;
                    // "monthly" already correct
                }
            }
            total += cost;
        }
        tvTotalMonthly.setText(String.format(Locale.getDefault(), "$%.2f / month", total));
    }

    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_insights) {
                startActivity(new Intent(this, InsightsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddSubscriptionActivity.class)));
    }
}