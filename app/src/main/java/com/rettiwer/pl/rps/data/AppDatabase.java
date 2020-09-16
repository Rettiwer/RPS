package com.rettiwer.pl.rps.data;

import com.rettiwer.pl.rps.data.dao.TripDao;
import com.rettiwer.pl.rps.data.dao.TripEventDao;
import com.rettiwer.pl.rps.data.model.Trip;
import com.rettiwer.pl.rps.data.model.TripEvent;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Trip.class, TripEvent.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TripDao tripDao();
    public abstract TripEventDao tripEventDao();
}