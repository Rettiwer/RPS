package com.rettiwer.pl.rps.data.dao;

import com.rettiwer.pl.rps.data.model.Trip;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TripDao {
    @Query("SELECT * FROM trip")
    Single<List<Trip>> getAll();

    @Query("SELECT * FROM trip WHERE id = :id")
    Single<Trip> get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable add(Trip trip);

    @Delete
    Completable remove(Trip trip);
}
