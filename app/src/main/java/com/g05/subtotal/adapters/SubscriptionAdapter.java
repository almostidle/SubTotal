package com.g05.subtotal.adapters;

/*
 * BUG FIX #5 (Crash — two separate issues merged into one adapter):
 *
 * Issue A — Wrong package:
 *   activities/SubscriptionAdapter.java used package "com.example.subtotal.adapters"
 *   instead of "com.g05.subtotal.adapters". The rest of the app (HomeActivity,
 *   imports) uses com.g05.subtotal. A class with the wrong package is treated as
 *   a completely different class — HomeActivity's import would either fail to
 *   compile or pick up the wrong class at runtime.
 *
 * Issue B — Non-existent getter methods:
 *   The broken adapter called subscription.getAppName(), subscription.getCost(),
 *   and subscription.getRenewalDate(). The Subscription model is a plain Java
 *   object with public fields (serviceName, price, nextBillDate), not JavaBean
 *   getters. Calling a method that doesn't exist = NoSuchMethodError at runtime.
 *
 * Issue C — Stub adapter in adapters package:
 *   adapters/SubscriptionAdapter.java existed but was completely empty —
 *   onCreateViewHolder returned a ViewHolder wrapping a blank View(), so the
 *   RecyclerView would render nothing and the app would look broken.
 *
 * Fix: one adapter, correct package, correct field access, correct ViewHolder
 *   that inflates item_subscription.xml and passes subscription data to
 *   SubDetailActivity on item click.
 *
 * ACTION REQUIRED: Delete activities/SubscriptionAdapter.java entirely.
 *   Replace adapters/SubscriptionAdapter.java with this file.
 */

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g05.subtotal.R;
import com.g05.subtotal.activities.SubDetailActivity;
import com.g05.subtotal.model.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {

    private List<Subscription> subscriptions = new ArrayList<>();

    /** Replace the current list and refresh the RecyclerView. */
    public void setSubscriptions(List<Subscription> list) {
        this.subscriptions = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subscription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subscription sub = subscriptions.get(position);

        // item_subscription.xml has tvAppName, tvCost, tvBillingCycle, tvRenewalDate
        holder.tvAppName.setText(sub.serviceName);
        holder.tvCost.setText(String.format(Locale.getDefault(), "$%.2f", sub.price));
        holder.tvBillingCycle.setText(sub.billingCycle);
        holder.tvRenewalDate.setText("Renews: " + sub.nextBillDate);

        // First letter as a coloured circle fallback (ivAppIcon is an ImageView in
        // item_subscription.xml — we leave it as the placeholder drawable for now;
        // Glide logo loading only happens inside SubDetailActivity).

        // Tap → open SubDetailActivity with all subscription data as extras
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SubDetailActivity.class);
            intent.putExtra(SubDetailActivity.EXTRA_ID,             sub.id);
            intent.putExtra(SubDetailActivity.EXTRA_SERVICE_NAME,   sub.serviceName);
            intent.putExtra(SubDetailActivity.EXTRA_PRICE,          sub.price);
            intent.putExtra(SubDetailActivity.EXTRA_BILLING_CYCLE,  sub.billingCycle);
            intent.putExtra(SubDetailActivity.EXTRA_CATEGORY,       sub.category);
            intent.putExtra(SubDetailActivity.EXTRA_NEXT_BILL_DATE, sub.nextBillDate);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvAppName;
        final TextView tvCost;
        final TextView tvBillingCycle;
        final TextView tvRenewalDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppName      = itemView.findViewById(R.id.tvAppName);
            tvCost         = itemView.findViewById(R.id.tvCost);
            tvBillingCycle = itemView.findViewById(R.id.tvBillingCycle);
            tvRenewalDate  = itemView.findViewById(R.id.tvRenewalDate);
        }
    }
}