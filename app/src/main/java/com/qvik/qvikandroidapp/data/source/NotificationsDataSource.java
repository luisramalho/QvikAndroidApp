package com.qvik.qvikandroidapp.data.source;

import android.support.annotation.NonNull;

import com.qvik.qvikandroidapp.data.Notification;

import java.util.List;

/**
 * Main entry point for accessing notifications data.
 */
public interface NotificationsDataSource {

    interface LoadNotificationsCallback {

        void onNotificationsLoaded(List<Notification> notifications);

        void onDataNotAvailable();
    }

    interface GetNotificationCallback {

        void onNotificationLoaded(Notification notification);

        void onDataNotAvailable();
    }

    void getNotifications(@NonNull LoadNotificationsCallback callback);

    void getNotification(@NonNull String notificationId,
                         @NonNull GetNotificationCallback callback);

    void saveNotification(@NonNull Notification notification);

    void deleteNotification(@NonNull String notificationId);

    void deleteAllNotifications();

    void refreshNotifications();
}
