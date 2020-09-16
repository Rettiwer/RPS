package com.rettiwer.pl.rps.data.dao;

import com.rettiwer.pl.rps.data.model.Trip;
import com.rettiwer.pl.rps.data.model.TripEvent;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TripEventDao {
    @Query("SELECT * FROM trip_event WHERE trip_id = :tripId")
    Single<List<TripEvent>> getAllByTrip(String tripId);

    @Query("SELECT * FROM trip_event WHERE trip_id = :tripId")
    List<TripEvent> getAllByTripBlocking(String tripId);

    @Query("SELECT * FROM trip_event WHERE id = :id")
    Single<TripEvent> get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable add(TripEvent tripEvent);

    @Delete
    Completable remove(TripEvent tripEvent);

    @Query("DELETE FROM trip_event WHERE trip_id = :tripId")
    Completable removeAllById(String tripId);
}
