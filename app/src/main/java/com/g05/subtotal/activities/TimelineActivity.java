package com.g05.subtotal.activities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TimelineActivity extends AppCompatActivity {

    private SubscriptionViewModel viewModel;
    private TimelineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        RecyclerView recyclerView = findViewById(R.id.rvTimeline);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TimelineAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        viewModel.allSubscriptions.observe(this, subscriptions -> {
            if (subscriptions != null) {
                adapter.setList(subscriptions);
            }
        });
    }

    private static class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
        private List<Subscription> list;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        private final Random random = new Random();

        TimelineAdapter(List<Subscription> list) {
            this.list = list;
        }

        void setList(List<Subscription> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Subscription sub = list.get(position);
            holder.tvServiceName.setText(sub.serviceName);
            holder.tvDate.setText(sub.nextBillDate);
            holder.tvPrice.setText(String.format("$%.2f", sub.price));

            if (sub.serviceName != null && !sub.serviceName.isEmpty()) {
                holder.tvLogoCircle.setText(sub.serviceName.substring(0, 1).toUpperCase());
            }

            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(color);
            holder.tvLogoCircle.setBackground(shape);

            try {
                Date dueDate = dateFormat.parse(sub.nextBillDate);
                Date today = new Date();
                if (dueDate != null) {
                    long diff = dueDate.getTime() - today.getTime();
                    long daysAway = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                    
                    if (daysAway < 0) {
                        holder.tvDaysAway.setText("Due " + Math.abs(daysAway) + " days ago");
                    } else {
                        holder.tvDaysAway.setText(daysAway + " days away");
                    }

                    if (daysAway >= 0 && daysAway <= 3) {
                        holder.tvSoonBadge.setVisibility(View.VISIBLE);
                    } else {
                        holder.tvSoonBadge.setVisibility(View.GONE);
                    }
                }
            } catch (ParseException e) {
                holder.tvDaysAway.setText("");
                holder.tvSoonBadge.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvLogoCircle, tvServiceName, tvDate, tvSoonBadge, tvDaysAway, tvPrice;

            ViewHolder(View itemView) {
                super(itemView);
                tvLogoCircle = itemView.findViewById(R.id.tvLogoCircle);
                tvServiceName = itemView.findViewById(R.id.tvServiceName);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvSoonBadge = itemView.findViewById(R.id.tvSoonBadge);
                tvDaysAway = itemView.findViewById(R.id.tvDaysAway);
                tvPrice = itemView.findViewById(R.id.tvPrice);
            }
        }
    }
}
