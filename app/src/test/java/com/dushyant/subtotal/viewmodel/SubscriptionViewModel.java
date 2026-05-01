package com.dushyant.subtotal.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dushyant.subtotal.database.AppDatabase;
import com.dushyant.subtotal.database.SubscriptionDao;
import com.dushyant.subtotal.model.Subscription;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubscriptionViewModel extends AndroidViewModel {

    private final SubscriptionDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public final LiveData<List<Subscription>> allSubscriptions;
    public final LiveData<Double> totalMonthlySpend;

    public SubscriptionViewModel(@NonNull Application application) {
        super(application);
        dao = AppDatabase.getInstance(application).subscriptionDao();
        allSubscriptions = dao.getAllSubscriptions();
        totalMonthlySpend = dao.getTotalMonthlySpend();
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