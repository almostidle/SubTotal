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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private SubscriptionViewModel subscriptionViewModel;
    private SubscriptionAdapter subscriptionAdapter;

    // Views - Empty state (S5)
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddEmpty;

    // Views - Full state (S6)
    private LinearLayout layoutFullState;
    private RecyclerView recyclerViewSubscriptions;
    private TextView tvTotalMonthly;
    private FloatingActionButton fabAdd;

    // Shared
    private TextView tvGreeting;
    private TextView tvDate;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setDateAndGreeting();
        setupRecyclerView();
        setupViewModel();
        setupBottomNav();
        setupFabs();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvDate = findViewById(R.id.tvDate);

        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        fabAddEmpty = findViewById(R.id.fabAddEmpty);

        layoutFullState = findViewById(R.id.layoutFullState);
        recyclerViewSubscriptions = findViewById(R.id.recyclerViewSubscriptions);
        tvTotalMonthly = findViewById(R.id.tvTotalMonthly);
        fabAdd = findViewById(R.id.fabAdd);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setDateAndGreeting() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        tvDate.setText(sdf.format(new Date()));
    }

    private void setupRecyclerView() {
        subscriptionAdapter = new SubscriptionAdapter(subscription -> {
            // Use the correct keys defined in SubDetailActivity
            Intent intent = new Intent(HomeActivity.this, SubDetailActivity.class);
            intent.putExtra(SubDetailActivity.EXTRA_ID,             subscription.getId());
            intent.putExtra(SubDetailActivity.EXTRA_SERVICE_NAME,   subscription.getServiceName());
            intent.putExtra(SubDetailActivity.EXTRA_PRICE,          subscription.getPrice());
            intent.putExtra(SubDetailActivity.EXTRA_BILLING_CYCLE,  subscription.getBillingCycle());
            intent.putExtra(SubDetailActivity.EXTRA_CATEGORY,       subscription.getCategory());
            intent.putExtra(SubDetailActivity.EXTRA_NEXT_BILL_DATE, subscription.getNextBillDate());
            startActivity(intent);
        });
        recyclerViewSubscriptions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSubscriptions.setAdapter(subscriptionAdapter);
        recyclerViewSubscriptions.setNestedScrollingEnabled(false);
    }

    private void setupViewModel() {
        subscriptionViewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        subscriptionViewModel.getAllSubscriptions().observe(this, subscriptions -> {
            subscriptionAdapter.setSubscriptions(subscriptions);

            if (subscriptions == null || subscriptions.isEmpty()) {
                showEmptyState();
            } else {
                showFullState(subscriptions);
            }
        });
    }

    /** S5 - Home Empty */
    private void showEmptyState() {
        layoutEmptyState.setVisibility(View.VISIBLE);
        layoutFullState.setVisibility(View.GONE);
        fabAdd.setVisibility(View.GONE);
    }

    /** S6 - Home Full */
    private void showFullState(List<Subscription> subscriptions) {
        layoutEmptyState.setVisibility(View.GONE);
        layoutFullState.setVisibility(View.VISIBLE);
        fabAdd.setVisibility(View.VISIBLE);

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
        tvTotalMonthly.setText(String.format(Locale.getDefault(), "$ %.0f/month", total));
    }

    private void setupFabs() {
        View.OnClickListener addClick = v ->
                startActivity(new Intent(HomeActivity.this, AddSubscriptionActivity.class));
        fabAdd.setOnClickListener(addClick);
        fabAddEmpty.setOnClickListener(addClick);
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
}
