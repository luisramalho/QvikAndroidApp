package com.qvik.qvikandroidapp.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

// TODO Improve the synchronisation between locally persisted data and data obtained from the server

/**
 * Concrete implementation to load qvikies from the data sources into a cache.
 */
public class QvikiesRepository implements QvikiesDataSource {

    private static QvikiesRepository instance = null;

    private final QvikiesDataSource qvikiesRemoteDataSource;

    private final QvikiesDataSource qvikiesLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Qvikie> cachedQvikies;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    private boolean cacheIsDirty = false;

    // Prevent direct instantiation.
    private QvikiesRepository(@NonNull QvikiesDataSource qvikiesRemoteDataSource,
                            @NonNull QvikiesDataSource qvikiesLocalDataSource) {
        //noinspection ResultOfMethodCallIgnored
        this.qvikiesRemoteDataSource = checkNotNull(qvikiesRemoteDataSource, "qvikiesRemoteDataSource == null");
        //noinspection ResultOfMethodCallIgnored
        this.qvikiesLocalDataSource = checkNotNull(qvikiesLocalDataSource, "qvikiesLocalDataSource == null");
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
        if (instance == null) {
            instance = new QvikiesRepository(qvikiesRemoteDataSource, qvikiesLocalDataSource);
        }
        return instance;
    }

    /**
     * Used to force {@link #getInstance(QvikiesDataSource, QvikiesDataSource)} to create a new instance
     * next time it's called.
     */
    static void destroyInstance() {
        instance = null;
    }

    /**
     * Gets qvikies from cache, local data source (realm) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadQvikiesCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getQvikies(@NonNull final LoadQvikiesCallback callback) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(callback, "callback == null");

        // Respond immediately with cache if available and not dirty
        if (cachedQvikies != null && !cacheIsDirty) {
            callback.onQvikiesLoaded(new ArrayList<>(cachedQvikies.values()));
            return;
        }

        EspressoIdlingResource.increment(); // App is busy until further notice

        if (cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getQvikiesFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            qvikiesLocalDataSource.getQvikies(new LoadQvikiesCallback() {
                @Override
                public void onQvikiesLoaded(List<Qvikie> qvikies) {
                    refreshCache(qvikies);

                    EspressoIdlingResource.decrement(); // Set app as idle.

                    callback.onQvikiesLoaded(new ArrayList<>(cachedQvikies.values()));
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
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(qvikieId, "qvikieId == null");
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(callback, "callback == null");

        Qvikie cachedQvikie = getQvikieWithId(qvikieId);

        // Respond immediately with cache if available
        if (cachedQvikie != null) {
            callback.onQvikieLoaded(cachedQvikie);
            return;
        }

        EspressoIdlingResource.increment(); // App is busy until further notice

        // Load from server/persisted if needed.

        // Is the qvikie in the local data source? If not, query the network.
        qvikiesLocalDataSource.getQvikie(qvikieId, new GetQvikieCallback() {
            @Override
            public void onQvikieLoaded(Qvikie qvikie) {
                if (qvikie == null) {
                    onDataNotAvailable();
                    return;
                }

                // Do in memory cache update to keep the app UI up to date
                if (cachedQvikies == null) {
                    cachedQvikies = new LinkedHashMap<>();
                }
                cachedQvikies.put(qvikie.getId(), qvikie);

                EspressoIdlingResource.decrement(); // Set app as idle.

                callback.onQvikieLoaded(qvikie);
            }

            @Override
            public void onDataNotAvailable() {
                qvikiesRemoteDataSource.getQvikie(qvikieId, new GetQvikieCallback() {
                    @Override
                    public void onQvikieLoaded(Qvikie qvikie) {
                        // Do in memory cache update to keep the app UI up to date
                        if (cachedQvikies == null) {
                            cachedQvikies = new LinkedHashMap<>();
                        }
                        cachedQvikies.put(qvikie.getId(), qvikie);

                        EspressoIdlingResource.decrement(); // Set app as idle.

                        callback.onQvikieLoaded(qvikie);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        EspressoIdlingResource.decrement(); // Set app as idle.

                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveQvikie(@NonNull Qvikie qvikie) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(qvikie, "qvikie == null");

        qvikiesRemoteDataSource.saveQvikie(qvikie);
        qvikiesLocalDataSource.saveQvikie(qvikie);

        // Do in memory cache update to keep the app UI up to date
        if (cachedQvikies == null) {
            cachedQvikies = new LinkedHashMap<>();
        }
        cachedQvikies.put(qvikie.getId(), qvikie);
    }

    @Override
    public void refreshQvikies() {
        cacheIsDirty = true;
    }

    @Override
    public void deleteAllQvikies() {
        qvikiesRemoteDataSource.deleteAllQvikies();
        qvikiesLocalDataSource.deleteAllQvikies();

        if (cachedQvikies == null) {
            cachedQvikies = new LinkedHashMap<>();
        }
        cachedQvikies.clear();
    }

    @Override
    public void deleteQvikie(@NonNull Qvikie qvikie) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(qvikie, "qvikie == null");

        qvikiesRemoteDataSource.deleteQvikie(qvikie);
        qvikiesLocalDataSource.deleteQvikie(qvikie);

        cachedQvikies.remove(qvikie);
    }

    @Override
    public void deleteQvikieById(@NonNull String qvikieId) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(qvikieId, "qvikieId == null");

        qvikiesRemoteDataSource.deleteQvikieById(qvikieId);
        qvikiesLocalDataSource.deleteQvikieById(qvikieId);

        cachedQvikies.remove(qvikieId);
    }

    private void getQvikiesFromRemoteDataSource(@NonNull final LoadQvikiesCallback callback) {
        qvikiesRemoteDataSource.getQvikies(new LoadQvikiesCallback() {
            @Override
            public void onQvikiesLoaded(List<Qvikie> qvikies) {
                refreshCache(qvikies);
                refreshLocalDataSource(qvikies);

                EspressoIdlingResource.decrement(); // Set app as idle.

                callback.onQvikiesLoaded(new ArrayList<>(cachedQvikies.values()));
            }

            @Override
            public void onDataNotAvailable() {
                EspressoIdlingResource.decrement(); // Set app as idle.

                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Qvikie> qvikies) {
        if (cachedQvikies == null) {
            cachedQvikies = new LinkedHashMap<>();
        }
        cachedQvikies.clear();
        for (Qvikie qvikie : qvikies) {
            cachedQvikies.put(qvikie.getId(), qvikie);
        }
        cacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Qvikie> qvikies) {
        qvikiesLocalDataSource.deleteAllQvikies();
        for (Qvikie qvikie : qvikies) {
            qvikiesLocalDataSource.saveQvikie(qvikie);
        }
    }

    @Nullable
    private Qvikie getQvikieWithId(@NonNull String qvikieId) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(qvikieId, "qvikieId == null");

        if (cachedQvikies == null || cachedQvikies.isEmpty()) {
            return null;
        } else {
            return cachedQvikies.get(qvikieId);
        }
    }
}
