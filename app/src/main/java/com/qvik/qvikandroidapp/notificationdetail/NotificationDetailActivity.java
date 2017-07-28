package com.qvik.qvikandroidapp.notificationdetail;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.qvik.qvikandroidapp.LifecycleAppCompatActivity;
import com.qvik.qvikandroidapp.ViewModelFactory;
import com.qvik.qvikandroidapp.qvikiedetail.QvikieDetailViewModel;

public class NotificationDetailActivity extends LifecycleAppCompatActivity
        implements NotificationDetailNavigator {

    public static final String EXTRA_NOTIFICATION_ID = "NOTIFICATION_ID";

    @Override
    public void onNotificationViewed() {

    }

    @NonNull
    public static NotificationDetailViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(NotificationDetailViewModel.class);
    }
}
