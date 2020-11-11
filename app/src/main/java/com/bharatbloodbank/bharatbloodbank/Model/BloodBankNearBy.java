package com.bharatbloodbank.bharatbloodbank.Model;

import com.google.android.gms.maps.model.LatLng;

public class BloodBankNearBy {
    private String formatted_address,name;
    private double rating;
    private int user_ratings_total;
    private LatLng User_latLng,BankLatLng;
    private boolean Open_Close;
    private String Time,Distance;

    public BloodBankNearBy() {
    }

    public BloodBankNearBy(String formatted_address, String name, double rating, int user_ratings_total, LatLng user_latLng, LatLng bankLatLng, boolean open_Close, String time, String distance) {
        this.formatted_address = formatted_address;
        this.name = name;
        this.rating = rating;
        this.user_ratings_total = user_ratings_total;
        User_latLng = user_latLng;
        BankLatLng = bankLatLng;
        Open_Close = open_Close;
        Time = time;
        Distance = distance;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public boolean isOpen_Close() {
        return Open_Close;
    }

    public void setOpen_Close(boolean open_Close) {
        Open_Close = open_Close;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getUser_ratings_total() {
        return user_ratings_total;
    }

    public void setUser_ratings_total(int user_ratings_total) {
        this.user_ratings_total = user_ratings_total;
    }

    public LatLng getUser_latLng() {
        return User_latLng;
    }

    public void setUser_latLng(LatLng user_latLng) {
        User_latLng = user_latLng;
    }

    public LatLng getBankLatLng() {
        return BankLatLng;
    }

    public void setBankLatLng(LatLng bankLatLng) {
        BankLatLng = bankLatLng;
    }
}
