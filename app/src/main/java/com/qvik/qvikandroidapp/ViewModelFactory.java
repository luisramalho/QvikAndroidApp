package com.qvik.qvikandroidapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.qvik.qvikandroidapp.addeditqvikie.AddEditQvikieViewModel;
import com.qvik.qvikandroidapp.data.source.NotificationsRepository;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;
import com.qvik.qvikandroidapp.notificationdetail.NotificationDetailViewModel;
import com.qvik.qvikandroidapp.notifications.NotificationsViewModel;
import com.qvik.qvikandroidapp.qvikiedetail.QvikieDetailViewModel;
import com.qvik.qvikandroidapp.qvikies.QvikiesViewModel;
import com.qvik.qvikandroidapp.statistics.StatisticsViewModel;

/**
 * A creator is used to inject the product ID into the ViewModel
 * <p>
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory instance;

    private final Application application;

    private final QvikiesRepository qvikiesRepository;

    private final NotificationsRepository notificationsRepository;

    public static ViewModelFactory getInstance(Application application) {

        if (instance == null) {
            synchronized (ViewModelFactory.class) {
                if (instance == null) {
                    instance = new ViewModelFactory(
                            application,
                            Injection.provideQvikiesRepository(application.getApplicationContext()),
                            Injection.provideNotificationsRepository(application.getApplicationContext())
                    );
                }
            }
        }
        return instance;
    }

    private ViewModelFactory(Application application,
                             QvikiesRepository qvikiesRepository,
                             NotificationsRepository notificationsRepository) {
        this.application = application;
        this.qvikiesRepository = qvikiesRepository;
        this.notificationsRepository = notificationsRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QvikieDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new QvikieDetailViewModel(application, qvikiesRepository);
        } else if (modelClass.isAssignableFrom(AddEditQvikieViewModel.class)) {
            //noinspection unchecked
            return (T) new AddEditQvikieViewModel(application, qvikiesRepository);
        } else if (modelClass.isAssignableFrom(QvikiesViewModel.class)) {
            //noinspection unchecked
            return (T) new QvikiesViewModel(application, qvikiesRepository);
        } else if (modelClass.isAssignableFrom(StatisticsViewModel.class)) {
            //noinspection unchecked
            return (T) new StatisticsViewModel(application, qvikiesRepository);
        } else if (modelClass.isAssignableFrom(NotificationsViewModel.class)) {
            //noinspection unchecked
            return (T) new NotificationsViewModel(application, notificationsRepository);
        } else if (modelClass.isAssignableFrom(NotificationDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new NotificationDetailViewModel(application, notificationsRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " +
                modelClass.getName());
    }
}
