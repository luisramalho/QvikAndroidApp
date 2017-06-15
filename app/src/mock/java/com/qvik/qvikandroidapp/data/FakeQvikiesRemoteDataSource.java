package com.qvik.qvikandroidapp.data;


import android.support.annotation.NonNull;

import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO Implementation of a remote data source with static access to the data for easy testing.
 */
public class FakeQvikiesRemoteDataSource implements QvikiesDataSource {

    private static FakeQvikiesRemoteDataSource INSTANCE;

    private static final Map<String, Qvikie> QVIKIES_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeQvikiesRemoteDataSource() {

    }

    public static FakeQvikiesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeQvikiesRemoteDataSource();
        }
        return INSTANCE;
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
