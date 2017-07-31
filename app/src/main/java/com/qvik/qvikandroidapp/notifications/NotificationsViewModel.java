package com.qvik.qvikandroidapp.notifications;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

import com.qvik.qvikandroidapp.SingleLiveEvent;
import com.qvik.qvikandroidapp.data.Notification;
import com.qvik.qvikandroidapp.data.source.NotificationsDataSource;
import com.qvik.qvikandroidapp.data.source.NotificationsDataSource.LoadNotificationsCallback;
import com.qvik.qvikandroidapp.data.source.NotificationsRepository;

import java.util.List;

public class NotificationsViewModel extends AndroidViewModel {

    public final ObservableList<Notification> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableBoolean empty = new ObservableBoolean(false);

    private final ObservableBoolean isDataLoadingError = new ObservableBoolean(false);

    private SingleLiveEvent<String> openNotificationEvent = new SingleLiveEvent<>();

    private Context context;

    private final NotificationsRepository notificationsRepository;

    public NotificationsViewModel(Application context, NotificationsRepository
            notificationsRepository) {
        super(context);
        this.context = context;
        this.notificationsRepository = notificationsRepository;
    }

    public void start() {
        loadNotifications(false);
    }

    public void loadNotifications(boolean forceUpdate) {
        loadNotifications(forceUpdate, true);
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link NotificationsDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadNotifications(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        if (forceUpdate) {
            notificationsRepository.refreshNotifications();
        }

        notificationsRepository.getNotifications(new LoadNotificationsCallback() {
            @Override
            public void onNotificationsLoaded(List<Notification> notifications) {
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                isDataLoadingError.set(false);

                items.clear();
                items.addAll(notifications);
                empty.set(items.isEmpty());
            }

            @Override
            public void onDataNotAvailable() {
                isDataLoadingError.set(true);
            }
        });
    }

    SingleLiveEvent<String> getOpenNotificationEvent() {
        return openNotificationEvent;
    }
}
