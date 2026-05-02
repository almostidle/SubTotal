package com.g05.subtotal.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.g05.subtotal.model.Subscription;

import java.util.List;

@Dao
public interface SubscriptionDao {

    @Insert
    void insert(Subscription subscription);

    @Delete
    void delete(Subscription subscription);

    @Query("SELECT * FROM subscriptions ORDER BY nextBillDate ASC")
    LiveData<List<Subscription>> getAllSubscriptions();

    @Query("SELECT SUM(price) FROM subscriptions WHERE billingCycle = 'Monthly'")
    LiveData<Double> getTotalMonthlySpend();

    @Query("SELECT * FROM subscriptions WHERE category = :category")
    LiveData<List<Subscription>> getByCategory(String category);

    @Query("SELECT SUM(price) FROM subscriptions WHERE category = :category AND billingCycle = 'Monthly'")
    LiveData<Double> getSpendByCategory(String category);
}
