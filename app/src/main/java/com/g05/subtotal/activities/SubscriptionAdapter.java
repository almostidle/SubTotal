package com.g05.subtotal.activities;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        this.subscriptions = subscriptions != null ? subscriptions : new ArrayList<>();
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
        holder.bind(subscriptions.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }

    static class SubscriptionViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivAppIcon;
        private final TextView tvAppName;
        private final TextView tvDueLabel;
        private final View viewDueDot;
        private final TextView tvCost;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAppIcon  = itemView.findViewById(R.id.ivAppIcon);
            tvAppName  = itemView.findViewById(R.id.tvAppName);
            tvDueLabel = itemView.findViewById(R.id.tvDueLabel);
            viewDueDot = itemView.findViewById(R.id.viewDueDot);
            tvCost     = itemView.findViewById(R.id.tvCost);
        }

        public void bind(Subscription subscription, OnItemClickListener listener) {
            tvAppName.setText(subscription.getServiceName());

            // Cost: "$ 24.99/month"
            String billingCycle = subscription.getBillingCycle() != null
                    ? subscription.getBillingCycle().toLowerCase()
                    : "monthly";
            tvCost.setText(String.format(Locale.getDefault(),
                    "$ %.2f/%s", subscription.getPrice(), billingCycle));

            // Due in X days label + colored dot
            int daysUntil = getDaysUntilRenewal(subscription.getNextBillDate());
            if (daysUntil < 0) {
                tvDueLabel.setText("Overdue");
                viewDueDot.setBackgroundColor(Color.parseColor("#E53935"));
            } else if (daysUntil == 0) {
                tvDueLabel.setText("Due Today");
                viewDueDot.setBackgroundColor(Color.parseColor("#E53935"));
            } else if (daysUntil <= 3) {
                tvDueLabel.setText("Due in " + daysUntil + " Day" + (daysUntil == 1 ? "" : "s"));
                viewDueDot.setBackgroundColor(Color.parseColor("#E53935")); // red — urgent
            } else {
                tvDueLabel.setText("Due in " + daysUntil + " Days");
                viewDueDot.setBackgroundColor(Color.parseColor("#4CAF50")); // green — upcoming
            }

            ivAppIcon.setImageResource(R.drawable.ic_app_placeholder);

            itemView.setOnClickListener(v -> listener.onItemClick(subscription));
        }

        private int getDaysUntilRenewal(String nextBillDate) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date renewal = sdf.parse(nextBillDate);
                if (renewal == null) return -1;

                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                long diffMs = renewal.getTime() - today.getTimeInMillis();
                return (int) (diffMs / (1000 * 60 * 60 * 24));
            } catch (Exception e) {
                return -1;
            }
        }
    }
}
