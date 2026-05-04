package com.g05.subtotal.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimelineActivity extends AppCompatActivity {

    private TimelineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Find views
        RecyclerView rv = findViewById(R.id.rvTimeline);
        View emptyState = findViewById(R.id.layoutEmptyState);

        // Setup RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TimelineAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        // Setup ViewModel to observe data
        SubscriptionViewModel viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        viewModel.getAllSubscriptions().observe(this, subs -> {

            // Check if list is empty to show empty state
            if (subs == null || subs.isEmpty()) {
                rv.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            } else {
                rv.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
                adapter.updateList(subs);
            }
        });

        setupBottomNav();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_timeline);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_timeline) {
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
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

    // Adapter class for the RecyclerView
    static class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.VH> {

        private List<Subscription> list;
        // Format to read the date from the database
        private final SimpleDateFormat dbFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        // Format to display the date on screen (e.g., "Feb 27")
        private final SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM d", Locale.getDefault());

        TimelineAdapter(List<Subscription> list) {
            this.list = list;
        }

        void updateList(List<Subscription> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_timeline, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            Subscription sub = list.get(position);

            // Set basic text
            if (holder.tvServiceName != null) {
                holder.tvServiceName.setText(sub.getServiceName());
            }

            // Format price with dollar sign
            if (holder.tvPrice != null) {
                holder.tvPrice.setText(String.format(Locale.getDefault(), "$ %.2f", sub.getPrice()));
            }

            // Set the logo letter
            String serviceName = sub.getServiceName();
            String letter = "?";
            if (serviceName != null && !serviceName.isEmpty()) {
                letter = String.valueOf(serviceName.charAt(0)).toUpperCase();
            }

            if (holder.tvLogoCircle != null) {
                holder.tvLogoCircle.setText(letter);

                // Set logo color based on category
                int color = Color.parseColor("#757575"); // Default color
                String category = sub.getCategory();
                if (category != null) {
                    if (category.equals("Entertainment")) color = Color.parseColor("#E53935");
                    else if (category.equals("Health")) color = Color.parseColor("#43A047");
                    else if (category.equals("Cloud")) color = Color.parseColor("#1E88E5");
                    else if (category.equals("Education")) color = Color.parseColor("#8E24AA");
                }

                if (holder.tvLogoCircle.getBackground() != null) {
                    holder.tvLogoCircle.getBackground().setTint(color);
                }
            }

            // Calculate days away and format date
            long daysAway = -1;
            try {
                String dateStr = sub.getNextBillDate();
                if (dateStr != null) {
                    Date due = dbFormat.parse(dateStr);
                    if (due != null) {
                        // Set formatted date (e.g., March 5)
                        if (holder.tvDate != null) {
                            holder.tvDate.setText(displayFormat.format(due));
                        }

                        // Calculate difference in days
                        long diffInMills = due.getTime() - new Date().getTime();
                        daysAway = TimeUnit.MILLISECONDS.toDays(diffInMills);
                    }
                }
            } catch (ParseException e) {
                // Fallback if parsing fails
                if (holder.tvDate != null) {
                    holder.tvDate.setText(sub.getNextBillDate());
                }
            }

            // Update UI based on days away
            if (holder.tvDaysAway != null) {
                if (daysAway >= 0) {
                    if (daysAway <= 3) {
                        holder.tvDaysAway.setText("Due in " + daysAway + " days");
                        if (holder.cvSoonBadge != null) holder.cvSoonBadge.setVisibility(View.VISIBLE);
                    } else {
                        // e.g., "06 days away"
                        holder.tvDaysAway.setText(String.format(Locale.getDefault(), "%02d days away", daysAway));
                        if (holder.cvSoonBadge != null) holder.cvSoonBadge.setVisibility(View.GONE);
                    }
                } else {
                    holder.tvDaysAway.setText("Overdue");
                    if (holder.cvSoonBadge != null) holder.cvSoonBadge.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvLogoCircle, tvServiceName, tvDate, tvDaysAway, tvPrice, cvSoonBadge;

            VH(View v) {
                super(v);
                tvLogoCircle  = v.findViewById(R.id.tvLogoCircle);
                tvServiceName = v.findViewById(R.id.tvServiceName);
                tvDate        = v.findViewById(R.id.tvDate);
                tvDaysAway    = v.findViewById(R.id.tvDaysAway);
                tvPrice       = v.findViewById(R.id.tvPrice);
                cvSoonBadge   = v.findViewById(R.id.cvSoonBadge);
            }
        }
    }
}