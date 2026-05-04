package com.g05.subtotal.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subscriptions")
public class Subscription {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String serviceName;
    private double price;
    private String billingCycle;   // "Monthly" or "Yearly"
    private String category;       // "Entertainment", "Health", "Cloud", "Other"
    private String nextBillDate;   // format: "dd/MM/yyyy"

    public Subscription(String serviceName, double price, String billingCycle,
                        String category, String nextBillDate) {
        this.serviceName = serviceName;
        this.price = price;
        this.billingCycle = billingCycle;
        this.category = category;
        this.nextBillDate = nextBillDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getServiceName() { return serviceName; }
    public String getAppName() { return serviceName; } // Alias for backward compatibility
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public double getPrice() { return price; }
    public double getCost() { return price; } // Alias for backward compatibility
    public void setPrice(double price) { this.price = price; }

    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getNextBillDate() { return nextBillDate; }
    public String getRenewalDate() { return nextBillDate; } // Alias for backward compatibility
    public void setNextBillDate(String nextBillDate) { this.nextBillDate = nextBillDate; }
}
