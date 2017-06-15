package com.qvik.qvikandroidapp.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Concrete implementation of a data source as a db (realm?).
 */
public class QvikiesLocalDataSource implements QvikiesDataSource {

    private static QvikiesLocalDataSource INSTANCE;

    private QvikiesDbHelper mDbHelper;

    public static QvikiesLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new QvikiesLocalDataSource(context);
        }
        return INSTANCE;
    }

    // Prevents instantiation
    private QvikiesLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new QvikiesDbHelper(context);
    }


    @Override
    public void getQvikies(@NonNull LoadQvikiesCallback callback) {

    }

    @Override
    public void getQvikie(@NonNull String qvikieId, @NonNull GetQvikieCallback callback) {

    }

    @Override
    public void saveQvikie(@NonNull Qvikie qvikie) {

    }

    @Override
    public void refreshQvikies() {

    }

    @Override
    public void deleteAllQvikies() {

    }

    @Override
    public void deleteQvikie(@NonNull String qvikieId) {

    }
}
