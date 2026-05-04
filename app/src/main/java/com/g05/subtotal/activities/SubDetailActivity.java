package com.g05.subtotal.activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.g05.subtotal.model.Subscription;
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
        put("youtube premium", "youtube.com");
        put("youtube music",   "youtube.com");
        put("apple music",     "apple.com");
        put("apple tv",        "apple.com");
        put("apple arcade",    "apple.com");
        put("icloud",          "apple.com");
        put("amazon prime",    "amazon.com");
        put("amazon",          "amazon.com");
        put("prime video",     "amazon.com");
        put("disney+",         "disneyplus.com");
        put("disney plus",     "disneyplus.com");
        put("hulu",            "hulu.com");
        put("hbo",             "hbo.com");
        put("hbo max",         "hbo.com");
        put("max",             "max.com");
        put("paramount+",      "paramountplus.com");
        put("peacock",         "peacocktv.com");
        put("twitch",          "twitch.tv");
        put("adobe",           "adobe.com");
        put("lightroom",       "adobe.com");
        put("light room",      "adobe.com");
        put("photoshop",       "adobe.com");
        put("illustrator",     "adobe.com");
        put("microsoft 365",   "microsoft.com");
        put("microsoft",       "microsoft.com");
        put("office 365",      "microsoft.com");
        put("xbox",            "xbox.com");
        put("xbox game pass",  "xbox.com");
        put("google one",      "google.com");
        put("google",          "google.com");
        put("google drive",    "google.com");
        put("dropbox",         "dropbox.com");
        put("notion",          "notion.so");
        put("figma",           "figma.com");
        put("slack",           "slack.com");
        put("zoom",            "zoom.us");
        put("github",          "github.com");
        put("linear",          "linear.app");
        put("1password",       "1password.com");
        put("nordvpn",         "nordvpn.com");
        put("expressvpn",      "expressvpn.com");
        put("duolingo",        "duolingo.com");
        put("headspace",       "headspace.com");
        put("calm",            "calm.com");
        put("audible",         "audible.com");
        put("kindle",          "amazon.com");
        put("playstation",     "playstation.com");
        put("ps plus",         "playstation.com");
        put("nintendo",        "nintendo.com");
        put("steam",           "steampowered.com");
        put("chatgpt",         "openai.com");
        put("openai",          "openai.com");
        put("claude",          "anthropic.com");
    }};

    private SubscriptionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_detail);

        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

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

            String letter = serviceName.length() > 0
                    ? String.valueOf(serviceName.charAt(0)).toUpperCase() : "?";
            tvLogo.setText(letter);
            tvLogo.getBackground().setTint(categoryColor(category));
            tvLogo.setVisibility(View.VISIBLE);
            ivLogo.setVisibility(View.GONE);

            String domain = DOMAIN_MAP.get(serviceName.trim().toLowerCase());
            if (domain != null) {
                String logoUrl = "https://logo.clearbit.com/" + domain;
                Glide.with(this)
                        .load(logoUrl)
                        .apply(new RequestOptions()
                                .circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e,
                                                        Object model, Target<Drawable> target,
                                                        boolean isFirstResource) {
                                tvLogo.setVisibility(View.VISIBLE);
                                ivLogo.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource,
                                                           Object model, Target<Drawable> target,
                                                           DataSource dataSource, boolean isFirstResource) {
                                tvLogo.setVisibility(View.GONE);
                                ivLogo.setVisibility(View.VISIBLE);
                                return false;
                            }
                        })
                        .into(ivLogo);
            }
        }

        tvPrice.setText(String.format(Locale.getDefault(), "$%.0f", price));
        if (nextBillDate != null) tvNextBill.setText(nextBillDate);
        if (billingCycle != null) tvBilling.setText(billingCycle);
        if (category != null)     tvCategory.setText(category);

        double annual = "Monthly".equals(billingCycle) ? price * 12 : price;
        tvAnnual.setText(String.format(Locale.getDefault(), "$ %.0f", annual));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        final String finalName = serviceName;
        final int    finalId   = id;
        final double finalPrice = price;
        final String finalBilling = billingCycle;
        final String finalCat  = category;
        final String finalDate = nextBillDate;

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (finalId == -1) {
                Toast.makeText(this, "Cannot delete: invalid ID", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("Delete Subscription")
                    .setMessage("Remove " + finalName + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        Subscription sub = new Subscription(
                                finalName, finalPrice, finalBilling, finalCat, finalDate);
                        sub.setId(finalId);
                        viewModel.delete(sub);
                        Toast.makeText(this, finalName + " deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
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
