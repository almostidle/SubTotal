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

        RecyclerView rv = findViewById(R.id.rvTimeline);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TimelineAdapter(new ArrayList<>());
        rv.setAdapter(adapter);

        SubscriptionViewModel viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        viewModel.getAllSubscriptions().observe(this, subs -> {
            if (subs == null || subs.isEmpty()) {
                adapter.updateList(getDummyData());
            } else {
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

    private List<Subscription> getDummyData() {
        List<Subscription> list = new ArrayList<>();
        list.add(new Subscription("Netflix",    24.99, "Monthly", "Entertainment", "04/05/2026"));
        list.add(new Subscription("Spotify",    12.99, "Monthly", "Health",        "08/05/2026"));
        list.add(new Subscription("Youtube",    13.99, "Monthly", "Entertainment", "22/05/2026"));
        list.add(new Subscription("Light Room", 19.99, "Monthly", "Cloud",         "30/05/2026"));
        return list;
    }

    static class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.VH> {

        private List<Subscription> list;
        private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        TimelineAdapter(List<Subscription> list) { this.list = list; }

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
        public void onBindViewHolder(VH h, int position) {
            Subscription sub = list.get(position);

            if (h.tvServiceName != null) h.tvServiceName.setText(sub.getServiceName());
            if (h.tvDate != null) h.tvDate.setText(sub.getNextBillDate());
            if (h.tvPrice != null) h.tvPrice.setText(String.format(Locale.getDefault(), "₹ %.2f", sub.getPrice()));

            String serviceName = sub.getServiceName();
            String letter = (serviceName != null && serviceName.length() > 0)
                    ? String.valueOf(serviceName.charAt(0)).toUpperCase() : "?";
            
            if (h.tvLogoCircle != null) {
                h.tvLogoCircle.setText(letter);
                int color;
                String category = sub.getCategory();
                switch (category != null ? category : "") {
                    case "Entertainment": color = Color.parseColor("#E53935"); break;
                    case "Health":        color = Color.parseColor("#43A047"); break;
                    case "Cloud":         color = Color.parseColor("#1E88E5"); break;
                    case "Education":     color = Color.parseColor("#8E24AA"); break;
                    default:              color = Color.parseColor("#757575"); break;
                }
                if (h.tvLogoCircle.getBackground() != null) {
                    h.tvLogoCircle.getBackground().setTint(color);
                }
            }

            long daysAway = -1;
            try {
                String dateStr = sub.getNextBillDate();
                if (dateStr != null) {
                    Date due = sdf.parse(dateStr);
                    if (due != null) {
                        long diff = due.getTime() - new Date().getTime();
                        daysAway = TimeUnit.MILLISECONDS.toDays(diff);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (h.tvDaysAway != null) {
                if (daysAway >= 0) {
                    if (daysAway <= 3) {
                        h.tvDaysAway.setText("Due in " + daysAway + " days");
                        if (h.cvSoonBadge != null) h.cvSoonBadge.setVisibility(View.VISIBLE);
                    } else {
                        h.tvDaysAway.setText(String.format(Locale.getDefault(), "%02d days away", daysAway));
                        if (h.cvSoonBadge != null) h.cvSoonBadge.setVisibility(View.GONE);
                    }
                } else {
                    h.tvDaysAway.setText("");
                    if (h.cvSoonBadge != null) h.cvSoonBadge.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvLogoCircle, tvServiceName, tvDate, tvDaysAway, tvPrice;
            View cvSoonBadge;
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
