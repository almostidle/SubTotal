package com.g05.subtotal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;
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
    private RecyclerView recyclerViewNotifications;
    private LinearLayout layoutEmptyNotifications;
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
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        layoutEmptyNotifications  = findViewById(R.id.layoutEmptyNotifications);
        bottomNavigationView      = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        subscriptionAdapter = new SubscriptionAdapter(subscription -> {
            Intent intent = new Intent(NotificationActivity.this, SubDetailActivity.class);
            intent.putExtra(SubDetailActivity.EXTRA_ID,             subscription.getId());
            intent.putExtra(SubDetailActivity.EXTRA_SERVICE_NAME,   subscription.getServiceName());
            intent.putExtra(SubDetailActivity.EXTRA_PRICE,          subscription.getPrice());
            intent.putExtra(SubDetailActivity.EXTRA_BILLING_CYCLE,  subscription.getBillingCycle());
            intent.putExtra(SubDetailActivity.EXTRA_CATEGORY,       subscription.getCategory());
            intent.putExtra(SubDetailActivity.EXTRA_NEXT_BILL_DATE, subscription.getNextBillDate());
            startActivity(intent);
        });
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotifications.setAdapter(subscriptionAdapter);
        recyclerViewNotifications.setNestedScrollingEnabled(false);
    }

    private void setupViewModel() {
        subscriptionViewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        subscriptionViewModel.getAllSubscriptions().observe(this, subscriptions -> {
            List<Subscription> upcoming = filterUpcomingRenewals(subscriptions);
            subscriptionAdapter.setSubscriptions(upcoming);

            if (upcoming.isEmpty()) {
                recyclerViewNotifications.setVisibility(View.GONE);
                layoutEmptyNotifications.setVisibility(View.VISIBLE);
            } else {
                recyclerViewNotifications.setVisibility(View.VISIBLE);
                layoutEmptyNotifications.setVisibility(View.GONE);
            }
        });
    }

    /** Returns only subscriptions renewing within the next 7 days */
    private List<Subscription> filterUpcomingRenewals(List<Subscription> all) {
        List<Subscription> upcoming = new ArrayList<>();
        if (all == null) return upcoming;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar sevenDaysLater = (Calendar) today.clone();
        sevenDaysLater.add(Calendar.DAY_OF_YEAR, 7);

        for (Subscription s : all) {
            try {
                Date renewalDate = sdf.parse(s.getNextBillDate());
                if (renewalDate != null) {
                    Calendar renewalCal = Calendar.getInstance();
                    renewalCal.setTime(renewalDate);
                    if (!renewalCal.before(today) && !renewalCal.after(sevenDaysLater)) {
                        upcoming.add(s);
                    }
                }
            } catch (Exception e) {
                // skip unparseable dates
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
            } else if (id == R.id.nav_timeline) {
                startActivity(new Intent(this, TimelineActivity.class));
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
