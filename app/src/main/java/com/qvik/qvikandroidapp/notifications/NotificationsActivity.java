package com.qvik.qvikandroidapp.notifications;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.qvik.qvikandroidapp.LifecycleAppCompatActivity;
import com.qvik.qvikandroidapp.ViewModelFactory;
import com.qvik.qvikandroidapp.notificationdetail.NotificationDetailActivity;

public class NotificationsActivity extends LifecycleAppCompatActivity
        implements NotificationItemNavigator {

    private NotificationsViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = obtainViewModel(this);
    }

    public static NotificationsViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(NotificationsViewModel.class);
    }

    @Override
    public void openNotificationDetail(String notificationId) {
        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.putExtra(NotificationDetailActivity.EXTRA_NOTIFICATION_ID, notificationId);
        startActivity(intent);
    }
}
