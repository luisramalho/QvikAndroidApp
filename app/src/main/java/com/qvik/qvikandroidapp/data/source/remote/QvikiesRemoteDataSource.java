package com.qvik.qvikandroidapp.data.source.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.source.QvikiesDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Finish firebase implementation of a remote data source
 */
public class QvikiesRemoteDataSource implements QvikiesDataSource {

    private static QvikiesRemoteDataSource INSTANCE;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference("qvikies");

    public static final String TAG = "QvikiesRemoteDataSource";

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
    public void getQvikies(@NonNull final LoadQvikiesCallback callback) {
        database.getReference("qvikies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Qvikie> qvikies = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    Qvikie qvikie = data.getValue(Qvikie.class);
                    qvikie.setId(data.getKey());
                    qvikies.add(qvikie);
                }
                callback.onQvikiesLoaded(qvikies);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void getQvikie(@NonNull final String qvikieId, @NonNull final GetQvikieCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onQvikieLoaded((Qvikie) dataSnapshot.child(qvikieId).getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void saveQvikie(@NonNull Qvikie qvikie) {
        databaseReference.setValue(qvikie);
    }

    @Override
    public void refreshQvikies() {
        // Not required because the {@link QvikiesRepository} handles the logic of refreshing the
        // qvikies from all the available data sources.
    }

    @Override
    public void deleteAllQvikies() {

    }

    @Override
    public void deleteQvikie(@NonNull String qvikieId) {

    }
}
