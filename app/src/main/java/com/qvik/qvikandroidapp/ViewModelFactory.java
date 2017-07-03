package com.qvik.qvikandroidapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.qvik.qvikandroidapp.addeditqvikie.AddEditQvikieViewModel;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;
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
    private static volatile ViewModelFactory INSTANCE;

    private final Application application;

    private final QvikiesRepository qvikiesRepository;

    public static ViewModelFactory getInstance(Application application) {

        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application,
                            Injection.provideQvikiesRepository(application.getApplicationContext()));
                }
            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application,
                             QvikiesRepository repository) {
        this.application = application;
        qvikiesRepository = repository;
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
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " +
                modelClass.getName());
    }
}
