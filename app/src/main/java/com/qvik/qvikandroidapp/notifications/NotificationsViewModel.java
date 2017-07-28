package com.qvik.qvikandroidapp.notifications;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.databinding.ObservableBoolean;

import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;

public class NotificationsViewModel extends AndroidViewModel {

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    private Context context;

    private final QvikiesRepository qvikiesRepository;

    public NotificationsViewModel(Application context, QvikiesRepository
            qvikiesRepository) {
        super(context);
        this.context = context;
        this.qvikiesRepository = qvikiesRepository;
    }

    public void start() {
        loadNotifications();
    }

    private void loadNotifications() {
        dataLoading.set(true);

        // TODO Get the notifications
        // qvikiesRepository.getNotifications(...);

        dataLoading.set(false);
    }

    public void loadNotifications(boolean forceUpdate) {
        loadNotifications(forceUpdate, true);
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link QvikiesDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadNotifications(boolean forceUpdate, boolean showLoadingUI) {
        // TODO implement
    }
}
