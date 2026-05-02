package com.g05.subtotal.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subscriptions")
public class Subscription {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String serviceName;
    public double price;
    public String billingCycle;   // "Monthly" or "Yearly"
    public String category;       // "Entertainment", "Health", "Cloud", "Other"
    public String nextBillDate;   // format: "dd/MM/yyyy"

    public Subscription(String serviceName, double price, String billingCycle,
                        String category, String nextBillDate) {
        this.serviceName = serviceName;
        this.price = price;
        this.billingCycle = billingCycle;
        this.category = category;
        this.nextBillDate = nextBillDate;
    }
}
