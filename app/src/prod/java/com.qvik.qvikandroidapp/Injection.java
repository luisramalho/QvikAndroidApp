package com.qvik.qvikandroidapp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.qvik.qvikandroidapp.data.source.QvikiesRepository;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.data.source.local.QvikDatabase;
import com.qvik.qvikandroidapp.data.source.local.QvikiesLocalDataSource;
import com.qvik.qvikandroidapp.data.source.remote.QvikiesRemoteDataSource;
import com.qvik.qvikandroidapp.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of production implementations for
 * {@link QvikiesDataSource} at compile time.
 */
public class Injection {

    public static QvikiesRepository provideQvikiesRepository(@NonNull Context context) {
        checkNotNull(context);
        QvikDatabase database = QvikDatabase.getInstance(context);
        return QvikiesRepository.getInstance(
                QvikiesRemoteDataSource.getInstance(),
                QvikiesLocalDataSource.getInstance(
                        new AppExecutors(),
                        database.qvikiesDao()));
    }
}
