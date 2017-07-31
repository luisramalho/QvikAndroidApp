package com.qvik.qvikandroidapp.data.source;

import android.support.annotation.NonNull;

import com.qvik.qvikandroidapp.data.Qvikie;

import java.util.List;

/**
 * Main entry point for accessing qvikies data.
 */
public interface QvikiesDataSource {

    interface LoadQvikiesCallback {

        void onQvikiesLoaded(List<Qvikie> qvikies);

        void onDataNotAvailable();
    }

    interface GetQvikieCallback {

        void onQvikieLoaded(Qvikie qvikie);

        void onDataNotAvailable();
    }

    void getQvikies(@NonNull LoadQvikiesCallback callback);

    void getQvikie(@NonNull String qvikieId, @NonNull GetQvikieCallback callback);

    void saveQvikie(@NonNull Qvikie qvikie);

    void refreshQvikies();

    void deleteAllQvikies();

    void deleteQvikie(@NonNull String qvikieId);
}
