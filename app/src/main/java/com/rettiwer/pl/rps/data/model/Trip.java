package com.rettiwer.pl.rps.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Trip {
    @NonNull
    @PrimaryKey
    private String id;
    @ColumnInfo(name = "first_driver")
    private String firstDriver;
    @ColumnInfo(name = "second_driver")
    private String secondDriver;
    @ColumnInfo(name = "vehicle_id")
    private String vehicleId;
    @ColumnInfo(name = "trailer_id")
    private String trailerId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstDriver() {
        return firstDriver;
    }

    public void setFirstDriver(String firstDriver) {
        this.firstDriver = firstDriver;
    }

    public String getSecondDriver() {
        return secondDriver;
    }

    public void setSecondDriver(String secondDriver) {
        this.secondDriver = secondDriver;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }
}
