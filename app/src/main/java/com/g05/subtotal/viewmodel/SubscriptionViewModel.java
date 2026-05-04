package com.g05.subtotal.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.g05.subtotal.database.AppDatabase;
import com.g05.subtotal.database.SubscriptionDao;
import com.g05.subtotal.model.Subscription;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubscriptionViewModel extends AndroidViewModel {

    private final SubscriptionDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final LiveData<List<Subscription>> allSubscriptions;
    private final LiveData<Double> totalMonthlySpend;

    public SubscriptionViewModel(@NonNull Application application) {
        super(application);
        dao = AppDatabase.getInstance(application).subscriptionDao();
        allSubscriptions = dao.getAllSubscriptions();
        totalMonthlySpend = dao.getTotalMonthlySpend();
    }

    public LiveData<List<Subscription>> getAllSubscriptions() {
        return allSubscriptions;
    }

    public LiveData<Double> getTotalMonthlySpend() {
        return totalMonthlySpend;
    }

    public void insert(Subscription subscription) {
        executor.execute(() -> dao.insert(subscription));
    }

    public void delete(Subscription subscription) {
        executor.execute(() -> dao.delete(subscription));
    }

    public LiveData<Double> getSpendByCategory(String category) {
        return dao.getSpendByCategory(category);
    }
}
