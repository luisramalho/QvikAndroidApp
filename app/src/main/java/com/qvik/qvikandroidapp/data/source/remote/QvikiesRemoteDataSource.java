package com.qvik.qvikandroidapp.data.source.remote;

import android.support.annotation.NonNull;

import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;

/**
 * TODO Implementation of a remote data source (firebase?)
 */
public class QvikiesRemoteDataSource implements QvikiesDataSource {

    private static QvikiesRemoteDataSource INSTANCE;

    public static QvikiesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QvikiesRemoteDataSource();
        }
        return INSTANCE;
    }

    private QvikiesRemoteDataSource() {
        // Prevents instantiation
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
