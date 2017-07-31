package com.qvik.qvikandroidapp.data.source.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qvik.qvikandroidapp.data.Notification;
import com.qvik.qvikandroidapp.data.source.NotificationsDataSource;

import java.util.ArrayList;
import java.util.List;

public class NotificationsRemoteDataSource implements NotificationsDataSource {

    private static final String TAG = "NotificationsRemoteDS";

    private static final String NOTIFICATIONS = "notifications";

    private static NotificationsRemoteDataSource instance;

    private DatabaseReference database;

    public static NotificationsRemoteDataSource getInstance() {
        if (instance == null) {
            instance = new NotificationsRemoteDataSource();
        }
        return instance;
    }

    private NotificationsRemoteDataSource() {
        // Prevents instantiation
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void getNotifications(
            @NonNull final LoadNotificationsCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    Notification notification = data.getValue(Notification.class);
                    if (notification != null) {
                        notification.setId(data.getKey());
                    }
                    notifications.add(notification);
                }

                callback.onNotificationsLoaded(notifications);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };
        database.child(NOTIFICATIONS).addListenerForSingleValueEvent(listener);
    }

    @Override
    public void getNotification(@NonNull final String notificationId,
                                @NonNull final GetNotificationCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onNotificationLoaded((Notification) dataSnapshot.child(notificationId).getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };
        database.child(NOTIFICATIONS).addListenerForSingleValueEvent(listener);
    }

    @Override
    public void saveNotification(@NonNull Notification notification) {
        database.child(NOTIFICATIONS).child(notification.getId()).setValue(notification);
    }

    @Override
    public void deleteNotification(@NonNull String notificationId) {
        database.child(NOTIFICATIONS).child(notificationId).removeValue();
    }

    @Override
    public void refreshNotifications() {
        // Not required because the {@link QvikiesRepository} handles the logic
        // of refreshing the notifications from all the available data sources.
    }
}
