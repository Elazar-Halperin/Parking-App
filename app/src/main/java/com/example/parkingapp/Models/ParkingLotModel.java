package com.example.parkingapp.Models;

import java.util.List;

public class ParkingLotModel {
    int floor;
    List<ParkingPlaceModel> list;

    public ParkingLotModel() {
    }

    public ParkingLotModel(int floor, List<ParkingPlaceModel> list) {
        this.floor = floor;
        this.list = list;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public List<ParkingPlaceModel> getList() {
        return list;
    }

    public void setList(List<ParkingPlaceModel> list) {
        this.list = list;
    }
}
