package com.qvik.qvikandroidapp.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.qvik.qvikandroidapp.data.Notification;
import com.qvik.qvikandroidapp.data.source.NotificationsDataSource;

import io.realm.Realm;
import io.realm.RealmQuery;

import static com.google.common.base.Preconditions.checkNotNull;

public class NotificationsLocalDataSource implements NotificationsDataSource {

    private static NotificationsLocalDataSource instance;

    private Realm realm;

    public static NotificationsLocalDataSource getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new NotificationsLocalDataSource(context);
        }
        return instance;
    }

    // Prevents instantiation
    private NotificationsLocalDataSource(@NonNull Context context) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(context, "context == null");
        realm = Realm.getDefaultInstance();
    }


    @Override
    public void getNotifications(@NonNull LoadNotificationsCallback callback) {
        RealmQuery<Notification> query = realm.where(Notification.class);
        callback.onNotificationsLoaded(query.findAll());
    }

    @Override
    public void getNotification(@NonNull String notificationId,
                                @NonNull GetNotificationCallback callback) {
        Notification notification = realm.where(Notification.class)
                .equalTo("id", notificationId).findFirst();
        callback.onNotificationLoaded(notification);
    }

    /**
     * Persists notification in a transaction.
     *
     * @param notification the notification to be persisted.
     */
    @Override
    public void saveNotification(@NonNull Notification notification) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(notification);
        realm.commitTransaction();
    }

    @Override
    public void deleteNotification(@NonNull String notificationId) {
        Notification notification = realm.where(Notification.class)
                .equalTo("id", notificationId).findFirst();
        realm.beginTransaction();
        notification.deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public void refreshNotifications() {
        // Not required because the {@link QvikiesRepository} handles the logic
        // of refreshing the notifications from all the available data sources.
    }
}
