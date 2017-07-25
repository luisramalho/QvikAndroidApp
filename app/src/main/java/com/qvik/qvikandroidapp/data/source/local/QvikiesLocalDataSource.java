package com.qvik.qvikandroidapp.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.QvikiesDao;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;
import com.qvik.qvikandroidapp.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class QvikiesLocalDataSource implements QvikiesDataSource {

    private static QvikiesLocalDataSource instance;

    private QvikiesDao qvikiesDao;

    private AppExecutors appExecutors;

    // Prevent direct instantiation.
    private QvikiesLocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull QvikiesDao qvikiesDao) {
        this.appExecutors = appExecutors;
        this.qvikiesDao = qvikiesDao;
    }

    public static QvikiesLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull QvikiesDao qvikiesDao) {
        if (instance == null) {
            instance = new QvikiesLocalDataSource(appExecutors, qvikiesDao);
        }
        return instance;
    }

    @Override
    public void getQvikies(@NonNull final LoadQvikiesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Qvikie> qvikies = qvikiesDao.getQvikies();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (qvikies.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onQvikiesLoaded(qvikies);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getQvikie(@NonNull final String qvikieId, @NonNull final GetQvikieCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Qvikie qvikie = qvikiesDao.getQvikieById(qvikieId);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (qvikie != null) {
                            callback.onQvikieLoaded(qvikie);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveQvikie(@NonNull final Qvikie qvikie) {
        checkNotNull(qvikie);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                qvikiesDao.insertQvikie(qvikie);
            }
        };
        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void refreshQvikies() {
        // Not required because the {@link QvikiesRepository} handles the logic of refreshing the
        // qvikies from all the available data sources.
    }

    @Override
    public void deleteAllQvikies() {
        Runnable deleteAllRunnable = new Runnable() {
            @Override
            public void run() {
                qvikiesDao.deleteQvikies();
            }
        };

        appExecutors.diskIO().execute(deleteAllRunnable);
    }

    @Override
    public void deleteQvikie(@NonNull final Qvikie qvikie) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                qvikiesDao.deleteQvikie(qvikie);
            }
        };

        appExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteQvikieById(@NonNull final String qvikieId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                qvikiesDao.deleteQvikieById(qvikieId);
            }
        };

        appExecutors.diskIO().execute(deleteRunnable);
    }

    @VisibleForTesting
    static void destroyInstance() {
        instance = null;
    }
}
