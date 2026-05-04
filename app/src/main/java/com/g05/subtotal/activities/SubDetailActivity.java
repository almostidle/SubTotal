package com.g05.subtotal.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.g05.subtotal.R;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ID             = "sub_id";
    public static final String EXTRA_SERVICE_NAME   = "sub_service";
    public static final String EXTRA_PRICE          = "sub_price";
    public static final String EXTRA_BILLING_CYCLE  = "sub_billing";
    public static final String EXTRA_CATEGORY       = "sub_category";
    public static final String EXTRA_NEXT_BILL_DATE = "sub_next_bill";

    private static final Map<String, String> DOMAIN_MAP = new HashMap<String, String>() {{
        put("netflix",         "netflix.com");
        put("spotify",         "spotify.com");
        put("youtube",         "youtube.com");
        put("apple music",     "apple.com");
        put("amazon prime",    "amazon.com");
        put("disney+",         "disneyplus.com");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_detail);

        int    id           = getIntent().getIntExtra(EXTRA_ID, -1);
        String serviceName  = getIntent().getStringExtra(EXTRA_SERVICE_NAME);
        double price        = getIntent().getDoubleExtra(EXTRA_PRICE, 0.0);
        String billingCycle = getIntent().getStringExtra(EXTRA_BILLING_CYCLE);
        String category     = getIntent().getStringExtra(EXTRA_CATEGORY);
        String nextBillDate = getIntent().getStringExtra(EXTRA_NEXT_BILL_DATE);

        TextView  tvLogo      = findViewById(R.id.tvDetailLogo);
        ImageView ivLogo      = findViewById(R.id.ivDetailLogo);
        TextView  tvName      = findViewById(R.id.tvDetailServiceName);
        TextView  tvPrice     = findViewById(R.id.tvDetailPrice);
        TextView  tvNextBill  = findViewById(R.id.tvDetailNextBill);
        TextView  tvBilling   = findViewById(R.id.tvDetailBilling);
        TextView  tvCategory  = findViewById(R.id.tvDetailCategory);
        TextView  tvAnnual    = findViewById(R.id.tvDetailAnnualCost);

        if (serviceName != null) {
            tvName.setText(serviceName);
            String letter = serviceName.isEmpty() ? "?" : serviceName.substring(0, 1).toUpperCase();
            tvLogo.setText(letter);
            tvLogo.getBackground().setTint(categoryColor(category));
            
            String domain = DOMAIN_MAP.get(serviceName.trim().toLowerCase());
            if (domain != null) {
                Glide.with(this)
                        .load("https://logo.clearbit.com/" + domain)
                        .apply(new RequestOptions().circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                tvLogo.setVisibility(View.GONE);
                                ivLogo.setVisibility(View.VISIBLE);
                                return false;
                            }
                        }).into(ivLogo);
            }
        }

        // Updated currency symbol to match HomeActivity
        tvPrice.setText(String.format(Locale.getDefault(), "₹%.2f", price));
        if (nextBillDate != null) tvNextBill.setText(nextBillDate);
        if (billingCycle != null) tvBilling.setText(billingCycle);
        if (category != null)     tvCategory.setText(category);

        double annual = "Monthly".equalsIgnoreCase(billingCycle) ? price * 12 : price;
        tvAnnual.setText(String.format(Locale.getDefault(), "₹ %.2f", annual));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (id == -1) {
                Toast.makeText(this, "Error: Cannot identify subscription", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, DeleteSubActivity.class);
            intent.putExtra(EXTRA_ID,             id);
            intent.putExtra(EXTRA_SERVICE_NAME,   serviceName);
            intent.putExtra(EXTRA_PRICE,          price);
            intent.putExtra(EXTRA_BILLING_CYCLE,  billingCycle);
            intent.putExtra(EXTRA_CATEGORY,       category);
            intent.putExtra(EXTRA_NEXT_BILL_DATE, nextBillDate);
            startActivity(intent);
        });
    }

    private int categoryColor(String category) {
        if (category == null) return Color.parseColor("#757575");
        switch (category) {
            case "Entertainment": return Color.parseColor("#E53935");
            case "Health":        return Color.parseColor("#43A047");
            case "Cloud":         return Color.parseColor("#1E88E5");
            case "Education":     return Color.parseColor("#8E24AA");
            default:              return Color.parseColor("#757575");
        }
    }
}
