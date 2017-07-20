package com.qvik.qvikandroidapp.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Data Access Object for the qvikies table.
 */
@Dao
public interface QvikiesDao {

    /**
     * Select all qvikies from the qvikies table.
     *
     * @return all qvikies.
     */
    @Query("SELECT * FROM qvikies")
    List<Qvikie> getQvikies();

    /**
     * Select qvikie from the qvikies table by ID.
     *
     * @return the qvikie.
     */
    @Query("SELECT * FROM qvikies WHERE id = :id")
    Qvikie getQvikieById(String id);

    /**
     * Insert a qvikie in the database.
     * If the qvikie already exists, replace it.
     *
     * @param qvikie the qvikie to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQvikie(Qvikie qvikie);

    /**
     * Delete a qvikie in the database by ID.
     *
     * @param id the qvikie ID to be deleted.
     */
    @Query("DELETE FROM qvikies WHERE id = :id")
    int deleteQvikieById(String id);

    /**
     * Delete a qvikie in the database.
     *
     * @param qvikie the qvikie to be deleted.
     */
    @Delete
    void deleteQvikie(Qvikie qvikie);

    /**
     * Delete all qvikies from the qvikis table.
     */
    @Query("DELETE FROM qvikies")
    void deleteQvikies();
}
