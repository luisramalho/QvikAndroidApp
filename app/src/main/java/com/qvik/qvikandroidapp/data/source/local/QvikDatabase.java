package com.qvik.qvikandroidapp.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.qvik.qvikandroidapp.data.Qvikie;
import com.qvik.qvikandroidapp.data.QvikiesDao;

/**
 * The Room Database that contains the Task table.
 */
@Database(entities = {Qvikie.class}, version = 1)
public abstract class QvikDatabase extends RoomDatabase {

    private static QvikDatabase instance;

    public abstract QvikiesDao qvikiesDao();

    private static final Object lock = new Object();

    public static QvikDatabase getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        QvikDatabase.class, "Qvik.db")
                        .build();
            }
            return instance;
        }
    }
}
