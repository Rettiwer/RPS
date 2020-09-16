package com.rettiwer.pl.rps.data.model;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trip_event")
public class TripEvent implements Comparable<TripEvent> {
    @NonNull
    @PrimaryKey
    private String id;
    @ColumnInfo(name = "event_type")
    private String eventType;
    @ColumnInfo(name = "departure_date")
    private String departureDate;
    @ColumnInfo(name = "arrival_date")
    private String arrivalDate;
    @ColumnInfo(name = "country")
    private String country;
    @ColumnInfo(name = "city")
    private String city;
    @ColumnInfo(name = "meter_status")
    private int meterStatus;
    @ColumnInfo(name = "weight")
    private int weight;
    @ColumnInfo(name = "comments")
    private String comments;
    @ColumnInfo(name = "trip_id")
    private String tripId;
    @ColumnInfo(name = "timestamp")
    private String timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getMeterStatus() {
        return meterStatus;
    }

    public void setMeterStatus(int meterStatus) {
        this.meterStatus = meterStatus;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(TripEvent tripEvent) {
        return Timestamp.valueOf(getTimestamp()).compareTo(Timestamp.valueOf(tripEvent.getTimestamp()));
    }
}
