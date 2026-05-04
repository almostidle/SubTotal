package com.example.subtotal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.subtotal.R;
import com.example.subtotal.adapters.SubscriptionAdapter;
import com.example.subtotal.model.Subscription;
import com.example.subtotal.viewmodel.SubscriptionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    private SubscriptionViewModel subscriptionViewModel;
    private SubscriptionAdapter subscriptionAdapter;
    private RecyclerView recyclerView;
    private LinearLayout emptyStateLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupBottomNav();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        emptyStateLayout = findViewById(R.id.layoutEmptyNotifications);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        subscriptionAdapter = new SubscriptionAdapter(subscription -> {
            Intent intent = new Intent(NotificationActivity.this, SubDetailActivity.class);
            intent.putExtra("subscription_id", subscription.getId());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(subscriptionAdapter);
    }

    private void setupViewModel() {
        subscriptionViewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        subscriptionViewModel.getAllSubscriptions().observe(this, subscriptions -> {
            List<Subscription> upcoming = filterUpcomingRenewals(subscriptions);
            subscriptionAdapter.setSubscriptions(upcoming);
            if (upcoming.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyStateLayout.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Filter subscriptions renewing in the next 7 days
     */
    private List<Subscription> filterUpcomingRenewals(List<Subscription> all) {
        List<Subscription> upcoming = new ArrayList<>();
        if (all == null) return upcoming;

        Calendar now = Calendar.getInstance();
        Calendar sevenDaysLater = Calendar.getInstance();
        sevenDaysLater.add(Calendar.DAY_OF_YEAR, 7);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Subscription s : all) {
            try {
                Date renewalDate = sdf.parse(s.getRenewalDate());
                if (renewalDate != null) {
                    Calendar renewalCal = Calendar.getInstance();
                    renewalCal.setTime(renewalDate);
                    if (!renewalCal.before(now) && !renewalCal.after(sevenDaysLater)) {
                        upcoming.add(s);
                    }
                }
            } catch (Exception e) {
                // skip malformed dates
            }
        }
        return upcoming;
    }

    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_notifications);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_notifications) {
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
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
