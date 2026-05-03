package com.g05.subtotal.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Subscription subscription);
    }

    private List<Subscription> subscriptions = new ArrayList<>();
    private final OnItemClickListener listener;

    public SubscriptionAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subscription, parent, false);
        return new SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder holder, int position) {
        Subscription subscription = subscriptions.get(position);
        holder.bind(subscription, listener);
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }

    static class SubscriptionViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAppName;
        private final TextView tvCost;
        private final TextView tvBillingCycle;
        private final TextView tvRenewalDate;
        private final ImageView ivAppIcon;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvCost = itemView.findViewById(R.id.tvCost);
            tvBillingCycle = itemView.findViewById(R.id.tvBillingCycle);
            tvRenewalDate = itemView.findViewById(R.id.tvRenewalDate);
            ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
        }

        public void bind(Subscription subscription, OnItemClickListener listener) {
            tvAppName.setText(subscription.getAppName());
            tvCost.setText(String.format("₹%.2f", subscription.getCost()));
            tvBillingCycle.setText(subscription.getBillingCycle());
            tvRenewalDate.setText("Renews: " + subscription.getRenewalDate());

            // Using a standard android resource if placeholder is missing
            ivAppIcon.setImageResource(android.R.drawable.ic_menu_agenda);

            itemView.setOnClickListener(v -> listener.onItemClick(subscription));
        }
    }
}
