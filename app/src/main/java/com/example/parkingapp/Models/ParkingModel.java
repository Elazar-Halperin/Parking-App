package com.example.parkingapp.Models;

import android.util.Log;

import java.io.Serializable;

public class ParkingModel implements Serializable {
    public static final int USUAL = 0;
    public static final int HANDICAPPED = 1;
    public static final int NULL = 2;

    public static final int maxRow = 10;
    public static final int maxColumn = 4;
    public static final int maxFloor = 3;


    public static final int NOT_TAKEN = 0;
    public static final int RESERVED = 1;
    public static final int TAKEN = 2;

    private String takenBy;
    private int status;
    private int type;
    private float width;
    private float height;

    private int floor;
    private int row;
    private int column;

    public ParkingModel(String takenBy, int status, int type, float width, float height, int floor, int row, int column) {
        this.takenBy = takenBy;
        this.status = status;
        this.type = type;
        this.width = width;
        this.height = height;
        this.floor = floor;
        this.row = row;
        this.column = column;
    }

    public ParkingModel() {
        this.takenBy = "";
        this.type = NULL;
    }

    // region getters and setters


    public void setTakenBy(String takenBy) {
        this.takenBy = takenBy;
    }

    public String getTakenBy() {
        return takenBy;
    }

    public void setId(String takenBy) {
        this.takenBy = takenBy;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
    // endregion

    @Override
    public String toString() {
        return "ParkingModel{" +
                "takenBy=" + takenBy +
                ", status=" + status +
                ", type=" + type +
                ", width=" + width +
                ", height=" + height +
                ", floor=" + floor +
                ", row=" + row +
                ", column=" + column +
                '}';
    }

    public static ParkingModel getNullParkingModel() {
        return new ParkingModel("none", 0, NULL, 0f, 0f, -1, -1, -1);
    }

    public int distance(ParkingModel parkingModel) {

        return Math.abs(this.distanceFromStart() - parkingModel.distanceFromStart());
    }

    public int distanceFromStart() {
        return (((maxRow )* 2 + 2) * (column / 4)
                + ((maxRow )* ((column / 2) % 2) * 2)
                + row * (int) Math.pow(-1, column / 2)) + maxColumn*(maxRow)* floor/2;
    }

    public ParkingModel SFC(ParkingModel[][][] parkingModels){
        ParkingModel startP = new ParkingModel();
        ParkingModel tempP = new ParkingModel();//רחוק ביותר
        tempP.setFloor(maxFloor - 1);
        tempP.setRow(maxRow - 2);
        tempP.setColumn(maxColumn - 1);
        startP = this;

        Log.d("SFC_startP", startP.toString());


        //searching for closest ללא תנאי מהתחלה
        for (int k = 0; k < maxFloor; k++) {
            for (int i = 0; i < maxRow - 1; i++) {
                for (int j = 0; j < maxColumn; j++) {
                    ParkingModel v = parkingModels[k][i][j];

                    if (v.getType() == ParkingModel.USUAL && ((v.getStatus() == ParkingModel.NOT_TAKEN))
                            && (startP.distance(tempP) > startP.distance(v))) {
                        tempP = v;
                        //if(k == 2 && i == 8 && j==3) {
                            Log.d("shit283", tempP.toString());

                    }
                }
            }
        }
        tempP.setStatus(parkingModels[tempP.getFloor()][tempP.getRow()][tempP.getColumn()].getStatus());
        tempP.setTakenBy(parkingModels[tempP.getFloor()][tempP.getRow()][tempP.getColumn()].getTakenBy());
        Log.d("SFC_tempP", tempP.toString());
        if (tempP.getStatus() == 0) return tempP;
        
        return new ParkingModel();
    }
}
