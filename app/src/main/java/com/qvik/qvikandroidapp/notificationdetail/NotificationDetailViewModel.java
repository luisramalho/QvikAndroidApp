package com.qvik.qvikandroidapp.notificationdetail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableField;

import com.qvik.qvikandroidapp.data.Notification;
import com.qvik.qvikandroidapp.data.source.NotificationsDataSource;
import com.qvik.qvikandroidapp.data.source.NotificationsRepository;
import com.qvik.qvikandroidapp.notifications.NotificationsFragment;

/**
 * Listens to user actions from the list item in ({@link NotificationsFragment})
 * and redirects them to the Fragment's actions listener.
 */
public class NotificationDetailViewModel extends AndroidViewModel
        implements NotificationsDataSource.GetNotificationCallback {

    public final ObservableField<Notification> notification = new ObservableField<>();

    private final NotificationsRepository notificationsRepository;

    private boolean isDataLoading;

    public NotificationDetailViewModel(
            Application context,
            NotificationsRepository notificationsRepository) {
        super(context);
        this.notificationsRepository = notificationsRepository;
    }

    public void start(String notificationId) {
        if (notificationId != null) {
            isDataLoading = true;
            notificationsRepository.getNotification(notificationId, this);
        }
    }

    public boolean isDataAvailable() {
        return notification.get() != null;
    }

    public boolean isDataLoading() {
        return isDataLoading;
    }

    @Override
    public void onNotificationLoaded(Notification notification) {
        this.notification.set(notification);
        isDataLoading = false;
    }

    @Override
    public void onDataNotAvailable() {
        notification.set(null);
        isDataLoading = false;
    }
}
