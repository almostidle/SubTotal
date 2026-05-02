package com.g05.subtotal.activities;

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

import java.util.ArrayList;
import java.util.List;

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
            adapter.updateList(subscriptions);
        });
    }

    // --- Adapter ---
    static class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

        private List<Subscription> list;

        TimelineAdapter(List<Subscription> list) {
            this.list = list;
        }

        void updateList(List<Subscription> newList) {
            this.list = newList;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_timeline, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Subscription sub = list.get(position);
            holder.tvServiceName.setText(sub.serviceName);
            holder.tvDate.setText("Due: " + sub.nextBillDate);
            holder.tvPrice.setText("$" + String.format("%.2f", sub.price) + "/mo");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvServiceName, tvDate, tvPrice;

            ViewHolder(View itemView) {
                super(itemView);
                tvServiceName = itemView.findViewById(R.id.tvServiceName);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvPrice = itemView.findViewById(R.id.tvPrice);
            }
        }
    }
}