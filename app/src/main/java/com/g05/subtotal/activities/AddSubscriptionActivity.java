package com.g05.subtotal.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.g05.subtotal.R;
import com.g05.subtotal.model.Subscription;
import com.g05.subtotal.viewmodel.SubscriptionViewModel;

import java.util.Calendar;

public class AddSubscriptionActivity extends AppCompatActivity {

    private static final int COLOR_BUTTON_DEFAULT  = Color.parseColor("#DEDAD6"); // warm grey pill
    private static final int COLOR_BUTTON_SELECTED = Color.parseColor("#1A1A1A"); // near-black
    private static final int COLOR_TEXT_DEFAULT    = Color.parseColor("#1A1A1A");
    private static final int COLOR_TEXT_SELECTED   = Color.parseColor("#FFFFFF");

    private EditText etServiceName, etPrice, etNextBillDate;
    private Button btnMonthly, btnYearly;
    private Button btnEntertainment, btnHealth, btnOther, btnCloud;

    private String selectedBillingCycle = "Monthly";
    private String selectedCategory     = "";

    private SubscriptionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subscription);

        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        etServiceName  = findViewById(R.id.etServiceName);
        etPrice        = findViewById(R.id.etPrice);
        etNextBillDate = findViewById(R.id.etNextBillDate);
        btnMonthly     = findViewById(R.id.btnMonthly);
        btnYearly      = findViewById(R.id.btnYearly);
        btnEntertainment = findViewById(R.id.btnEntertainment);
        btnHealth      = findViewById(R.id.btnHealth);
        btnOther       = findViewById(R.id.btnOther);
        btnCloud       = findViewById(R.id.btnCloud);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        etNextBillDate.setOnClickListener(v -> showDatePicker());

        btnMonthly.setOnClickListener(v -> selectBillingCycle("Monthly"));
        btnYearly.setOnClickListener(v  -> selectBillingCycle("Yearly"));
        selectBillingCycle("Monthly"); // default

        btnEntertainment.setOnClickListener(v -> selectCategory("Entertainment"));
        btnHealth.setOnClickListener(v        -> selectCategory("Health"));
        btnOther.setOnClickListener(v         -> selectCategory("Other"));
        btnCloud.setOnClickListener(v         -> selectCategory("Cloud"));

        findViewById(R.id.btnAddSubscription).setOnClickListener(v -> attemptAdd());
    }

    private void selectBillingCycle(String cycle) {
        selectedBillingCycle = cycle;
        applyButtonState(btnMonthly, "Monthly".equals(cycle));
        applyButtonState(btnYearly,  "Yearly".equals(cycle));
    }

    private void selectCategory(String category) {
        selectedCategory = category;
        applyButtonState(btnEntertainment, "Entertainment".equals(category));
        applyButtonState(btnHealth,        "Health".equals(category));
        applyButtonState(btnOther,         "Other".equals(category));
        applyButtonState(btnCloud,         "Cloud".equals(category));
    }

    private void applyButtonState(Button btn, boolean selected) {
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                selected ? COLOR_BUTTON_SELECTED : COLOR_BUTTON_DEFAULT));
        btn.setTextColor(selected ? COLOR_TEXT_SELECTED : COLOR_TEXT_DEFAULT);
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format("%02d/%02d/%04d", day, month + 1, year);
            etNextBillDate.setText(date);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void attemptAdd() {
        String name     = etServiceName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String date     = etNextBillDate.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etServiceName.setError("Enter service name");
            return;
        }
        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Enter price");
            return;
        }
        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please select a next bill date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(selectedCategory)) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price");
            return;
        }

        Subscription sub = new Subscription(name, price, selectedBillingCycle, selectedCategory, date);
        viewModel.insert(sub);

        Intent intent = new Intent(this, SuccessActivity.class);
        intent.putExtra("service",      name);
        intent.putExtra("price",        price);
        intent.putExtra("billingCycle", selectedBillingCycle);
        intent.putExtra("category",     selectedCategory);
        intent.putExtra("nextBillDate", date);
        startActivity(intent);
        finish();
    }
}