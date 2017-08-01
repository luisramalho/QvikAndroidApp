package com.qvik.qvikandroidapp.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
                    refreshLocalDataSource(notifications);

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

    /**
     * Gets notifications from the local data source (realm) unless the table is
     * new or empty. In that case it uses the network data source.
     * This is done to simplify the sample.
     * <p>
     * Note: {@link GetNotificationCallback#onDataNotAvailable()} is fired if
     * both data sources fail to get the data.
     */
    @Override
    public void getNotification(@NonNull final String notificationId,
                                @NonNull final GetNotificationCallback callback) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(notificationId, "notificationId == null");
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(callback, "callback == null");

        Notification cachedNotification = getNotificationWithId(notificationId);

        // Respond immediately with cache if available
        if (cachedNotification != null) {
            callback.onNotificationLoaded(cachedNotification);
            return;
        }

        EspressoIdlingResource.increment(); // App is busy until further notice

        // Load from server/persisted if needed.

        // Is the notification in the local data source? If not, query the network.
        notificationsLocalDataSource.getNotification(notificationId, new GetNotificationCallback() {
            @Override
            public void onNotificationLoaded(Notification notification) {
                if (notification == null) {
                    onDataNotAvailable();
                    return;
                }

                // Do in memory cache update to keep the app UI up to date
                if (cachedNotifications == null) {
                    cachedNotifications = new LinkedHashMap<>();
                }
                cachedNotifications.put(notification.getId(), notification);

                EspressoIdlingResource.decrement(); // Set app as idle.

                callback.onNotificationLoaded(notification);
            }

            @Override
            public void onDataNotAvailable() {
                notificationsRemoteDataSource.getNotification(notificationId, new GetNotificationCallback() {
                    @Override
                    public void onNotificationLoaded(Notification notification) {
                        // Do in memory cache update to keep the app UI up to date
                        if (cachedNotifications == null) {
                            cachedNotifications = new LinkedHashMap<>();
                        }
                        cachedNotifications.put(notification.getId(), notification);

                        EspressoIdlingResource.decrement(); // Set app as idle.

                        callback.onNotificationLoaded(notification);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        EspressoIdlingResource.decrement(); // Set app as idle.

                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveNotification(@NonNull Notification notification) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(notification, "notification == null");

        notificationsRemoteDataSource.saveNotification(notification);
        notificationsLocalDataSource.saveNotification(notification);

        // Do in memory cache update to keep the app UI up to date
        if (cachedNotifications == null) {
            cachedNotifications = new LinkedHashMap<>();
        }
        cachedNotifications.put(notification.getId(), notification);
    }

    @Override
    public void deleteNotification(@NonNull String notificationId) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(notificationId, "notificationId == null");

        notificationsRemoteDataSource.deleteNotification(notificationId);
        notificationsLocalDataSource.deleteNotification(notificationId);

        cachedNotifications.remove(notificationId);
    }

    @Override
    public void deleteAllNotifications() {
        notificationsRemoteDataSource.deleteAllNotifications();
        notificationsLocalDataSource.deleteAllNotifications();

        if (cachedNotifications== null) {
            cachedNotifications = new LinkedHashMap<>();
        }
        cachedNotifications.clear();
    }

    @Override
    public void refreshNotifications() {
        cacheIsDirty = true;
    }

    private void getNotificationsFromRemoteDataSource(
            @NonNull final LoadNotificationsCallback callback) {
        notificationsRemoteDataSource.getNotifications(new LoadNotificationsCallback() {
            @Override
            public void onNotificationsLoaded(List<Notification> notifications) {
                refreshCache(notifications);
                refreshLocalDataSource(notifications);

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

    private void refreshLocalDataSource(List<Notification> notifications) {
        notificationsLocalDataSource.deleteAllNotifications();
        for (Notification notification : notifications) {
            notificationsLocalDataSource.saveNotification(notification);
        }
    }

    @Nullable
    private Notification getNotificationWithId(@NonNull String notificationId) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(notificationId, "notificationId == null");

        if (cachedNotifications == null || cachedNotifications.isEmpty()) {
            return null;
        } else {
            return cachedNotifications.get(notificationId);
        }
    }
}
