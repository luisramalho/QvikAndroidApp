package com.qvik.qvikandroidapp.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.qvik.qvikandroidapp.data.Qvikie;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load qvikies from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class QvikiesRepository implements QvikiesDataSource {

    private static QvikiesRepository INSTANCE = null;

    private final QvikiesDataSource mQvikiesRemoteDataSource;

    private final QvikiesDataSource mQvikiesLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Qvikie> mCachedQvikies;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private QvikiesRepository(@NonNull QvikiesDataSource qvikiesRemoteDataSource,
                            @NonNull QvikiesDataSource qvikiesLocalDataSource) {
        mQvikiesRemoteDataSource = checkNotNull(qvikiesRemoteDataSource);
        mQvikiesLocalDataSource = checkNotNull(qvikiesLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param qvikiesRemoteDataSource the backend data source
     * @param qvikiesLocalDataSource  the device storage data source
     * @return the {@link QvikiesRepository} instance
     */
    public static QvikiesRepository getInstance(QvikiesDataSource qvikiesRemoteDataSource,
                                                QvikiesDataSource qvikiesLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new QvikiesRepository(qvikiesRemoteDataSource, qvikiesLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(QvikiesDataSource, QvikiesDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets qvikies from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadQvikiesCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getQvikies(@NonNull final LoadQvikiesCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedQvikies != null && !mCacheIsDirty) {
            callback.onQvikiesLoaded(new ArrayList<>(mCachedQvikies.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getQvikiesFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mQvikiesLocalDataSource.getQvikies(new LoadQvikiesCallback() {
                @Override
                public void onQvikiesLoaded(List<Qvikie> qvikies) {
                    refreshCache(qvikies);
                    callback.onQvikiesLoaded(new ArrayList<>(mCachedQvikies.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getQvikiesFromRemoteDataSource(callback);
                }
            });
        }
    }

    /**
     * Gets qvikies from local data source (realm) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link GetQvikieCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getQvikie(@NonNull final String qvikieId, @NonNull final GetQvikieCallback callback) {
        checkNotNull(qvikieId);
        checkNotNull(callback);

        Qvikie cachedQvikie = getQvikieWithId(qvikieId);

        // Respond immediately with cache if available
        if (cachedQvikie != null) {
            callback.onQvikieLoaded(cachedQvikie);
            return;
        }

        // Load from server/persisted if needed.

        // Is the qvikie in the local data source? If not, query the network.
        mQvikiesLocalDataSource.getQvikie(qvikieId, new GetQvikieCallback() {
            @Override
            public void onQvikieLoaded(Qvikie qvikie) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedQvikies == null) {
                    mCachedQvikies = new LinkedHashMap<>();
                }
                mCachedQvikies.put(qvikie.getId(), qvikie);
                callback.onQvikieLoaded(qvikie);
            }

            @Override
            public void onDataNotAvailable() {
                mQvikiesRemoteDataSource.getQvikie(qvikieId, new GetQvikieCallback() {
                    @Override
                    public void onQvikieLoaded(Qvikie qvikie) {
                        // Do in memory cache update to keep the app UI up to date
                        if (mCachedQvikies == null) {
                            mCachedQvikies = new LinkedHashMap<>();
                        }
                        mCachedQvikies.put(qvikie.getId(), qvikie);
                        callback.onQvikieLoaded(qvikie);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveQvikie(@NonNull Qvikie qvikie) {
        checkNotNull(qvikie);
        mQvikiesRemoteDataSource.saveQvikie(qvikie);
        mQvikiesLocalDataSource.saveQvikie(qvikie);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedQvikies == null) {
            mCachedQvikies = new LinkedHashMap<>();
        }
        mCachedQvikies.put(qvikie.getId(), qvikie);
    }

    @Override
    public void refreshQvikies() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllQvikies() {
        mQvikiesRemoteDataSource.deleteAllQvikies();
        mQvikiesLocalDataSource.deleteAllQvikies();

        if (mCachedQvikies == null) {
            mCachedQvikies = new LinkedHashMap<>();
        }
        mCachedQvikies.clear();
    }

    @Override
    public void deleteQvikie(@NonNull String qvikieId) {
        mQvikiesRemoteDataSource.deleteQvikie(checkNotNull(qvikieId));
        mQvikiesLocalDataSource.deleteQvikie(checkNotNull(qvikieId));

        mCachedQvikies.remove(qvikieId);
    }

    private void getQvikiesFromRemoteDataSource(@NonNull final LoadQvikiesCallback callback) {
        mQvikiesRemoteDataSource.getQvikies(new LoadQvikiesCallback() {
            @Override
            public void onQvikiesLoaded(List<Qvikie> qvikies) {
                refreshCache(qvikies);
                refreshLocalDataSource(qvikies);
                callback.onQvikiesLoaded(new ArrayList<>(mCachedQvikies.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Qvikie> qvikies) {
        if (mCachedQvikies == null) {
            mCachedQvikies = new LinkedHashMap<>();
        }
        mCachedQvikies.clear();
        for (Qvikie qvikie : qvikies) {
            mCachedQvikies.put(qvikie.getId(), qvikie);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Qvikie> qvikies) {
        mQvikiesLocalDataSource.deleteAllQvikies();
        for (Qvikie qvikie : qvikies) {
            mQvikiesLocalDataSource.saveQvikie(qvikie);
        }
    }

    @Nullable
    private Qvikie getQvikieWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedQvikies == null || mCachedQvikies.isEmpty()) {
            return null;
        } else {
            return mCachedQvikies.get(id);
        }
    }
}
