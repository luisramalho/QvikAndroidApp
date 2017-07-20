package com.qvik.qvikandroidapp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.qvik.qvikandroidapp.data.FakeQvikiesRemoteDataSource;
import com.qvik.qvikandroidapp.data.source.QvikiesRepository;
import com.qvik.qvikandroidapp.data.source.local.QvikDatabase;
import com.qvik.qvikandroidapp.data.source.local.QvikiesLocalDataSource;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link QvikiesDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static QvikiesRepository provideQvikiesRepository(@NonNull Context context) {
        checkNotNull(context);
        QvikDatabase database = QvikDatabase.getInstance(context);
        return QvikiesRepository.getInstance(
                FakeQvikiesRemoteDataSource.getInstance(),
                QvikiesLocalDataSource.getInstance(new AppExecutors(), database.qvikiesDao()));
    }
}
