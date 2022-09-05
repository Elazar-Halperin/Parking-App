package com.example.parkingapp.Models;

public class ParkingPlaceModel {
    int id;
    boolean isParkingForDisabled;
    boolean isTaken;

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public ParkingPlaceModel() {
    }

    public ParkingPlaceModel(int id , boolean isParkingForDisabled) {
        this.id = id;
        this.isParkingForDisabled = isParkingForDisabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isParkingForDisabled() {
        return isParkingForDisabled;
    }

    public void setParkingForDisabled(boolean parkingForDisabled) {
        isParkingForDisabled = parkingForDisabled;
    }
}
