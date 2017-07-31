package com.qvik.qvikandroidapp.data.source;

import android.support.annotation.NonNull;

import com.qvik.qvikandroidapp.data.Notification;
import com.qvik.qvikandroidapp.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load notifications from the data sources into a cache.
 */
public class NotificationsRepository implements NotificationsDataSource {

    private static NotificationsRepository instance = null;

    private final NotificationsDataSource notificationsRemoteDataSource;

    private final NotificationsDataSource notificationsLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Notification> cachedNotifications;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    private boolean cacheIsDirty = false;

    // Prevent direct instantiation.
    private NotificationsRepository(
            @NonNull NotificationsDataSource notificationsRemoteDataSource,
            @NonNull NotificationsDataSource notificationsLocalDataSource) {
        //noinspection ResultOfMethodCallIgnored
        this.notificationsRemoteDataSource = checkNotNull(
                notificationsRemoteDataSource,
                "notificationsRemoteDataSource == null");
        //noinspection ResultOfMethodCallIgnored
        this.notificationsLocalDataSource = checkNotNull(
                notificationsLocalDataSource,
                "notificationsLocalDataSource == null");
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param notificationsRemoteDataSource the backend data source
     * @param notificationsLocalDataSource  the device storage data source
     * @return the {@link NotificationsRepository} instance
     */
    public static NotificationsRepository getInstance(
            NotificationsDataSource notificationsRemoteDataSource,
            NotificationsDataSource notificationsLocalDataSource) {
        if (instance == null) {
            instance = new NotificationsRepository(
                    notificationsRemoteDataSource, notificationsLocalDataSource);
        }
        return instance;
    }

    /**
     * Used to force {@link #getInstance(NotificationsDataSource, NotificationsDataSource)}
     * to create a new instance next time it's called.
     */
    static void destroyInstance() {
        instance = null;
    }

    @Override
    public void getNotifications(@NonNull final LoadNotificationsCallback callback) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(callback, "callback == null");

        // Respond immediately with cache if available and not dirty
        if (cachedNotifications != null && !cacheIsDirty) {
            callback.onNotificationsLoaded(new ArrayList<>(cachedNotifications.values()));
            return;
        }

        EspressoIdlingResource.increment(); // App is busy until further notice

        if (cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getNotificationsFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            notificationsLocalDataSource.getNotifications(new LoadNotificationsCallback() {
                @Override
                public void onNotificationsLoaded(List<Notification> notifications) {
                    refreshCache(notifications);

                    EspressoIdlingResource.decrement(); // Set app as idle.

                    callback.onNotificationsLoaded(new ArrayList<>(
                            cachedNotifications.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getNotificationsFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void getNotification(@NonNull String notificationId,
                                @NonNull GetNotificationCallback callback) {
        // TODO
    }

    @Override
    public void saveNotification(@NonNull Notification notification) {
        // TODO
    }

    @Override
    public void deleteNotification(@NonNull String notificationId) {
        // TODO
    }

    @Override
    public void refreshNotifications() {
        cacheIsDirty = true;
    }

    private void getNotificationsFromRemoteDataSource(@NonNull final LoadNotificationsCallback callback) {
        notificationsRemoteDataSource.getNotifications(new LoadNotificationsCallback() {
            @Override
            public void onNotificationsLoaded(List<Notification> notifications) {
                refreshCache(notifications);
//                refreshLocalDataSource(notifications);

                EspressoIdlingResource.decrement(); // Set app as idle.

                callback.onNotificationsLoaded(new ArrayList<>(cachedNotifications.values()));
            }

            @Override
            public void onDataNotAvailable() {
                EspressoIdlingResource.decrement(); // Set app as idle.

                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Notification> notifications) {
        if (cachedNotifications == null) {
            cachedNotifications = new LinkedHashMap<>();
        }
        cachedNotifications.clear();
        for (Notification notification : notifications) {
            cachedNotifications.put(notification.getId(), notification);
        }
        cacheIsDirty = false;
    }
}
