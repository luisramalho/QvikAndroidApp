package com.qvik.qvikandroidapp.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Concrete implementation of a data source as a db (realm?).
 */
public class QvikiesLocalDataSource implements QvikiesDataSource {

    private static QvikiesLocalDataSource INSTANCE;

    private Realm realm;

    public static QvikiesLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new QvikiesLocalDataSource(context);
        }
        return INSTANCE;
    }

    // Prevents instantiation
    private QvikiesLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void getQvikies(@NonNull LoadQvikiesCallback callback) {
        RealmQuery<Qvikie> query = realm.where(Qvikie.class);
        callback.onQvikiesLoaded(query.findAll());
    }

    @Override
    public void getQvikie(@NonNull String qvikieId, @NonNull GetQvikieCallback callback) {
        Qvikie qvikie = realm.where(Qvikie.class).equalTo("id", qvikieId).findFirst();
        callback.onQvikieLoaded(qvikie);
    }

    /**
     * Persists qvikie in a transaction.
     *
     * @param qvikie the qvikie to be persisted.
     */
    @Override
    public void saveQvikie(@NonNull Qvikie qvikie) {
        realm.beginTransaction();
        realm.copyToRealm(qvikie);
        realm.commitTransaction();
    }

    @Override
    public void refreshQvikies() {
        // Not required because the {@link QvikiesRepository} handles the logic of refreshing the
        // qvikies from all the available data sources.
    }

    @Override
    public void deleteAllQvikies() {
        RealmResults<Qvikie> qvikies = realm.where(Qvikie.class).findAll();
        realm.beginTransaction();
        qvikies.deleteAllFromRealm();
        realm.commitTransaction();
    }

    @Override
    public void deleteQvikie(@NonNull String qvikieId) {
        Qvikie qvikie = realm.where(Qvikie.class).equalTo("id", qvikieId).findFirst();
        qvikie.deleteFromRealm();
    }
}
