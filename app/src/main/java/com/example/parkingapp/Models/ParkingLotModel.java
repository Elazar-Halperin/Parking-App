package com.example.parkingapp.Models;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.graphics.PaintKt;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParkingLotModel {

    public static final String UID = "-NCGBhWK9B9pBYztfLmX";

    int columns = 4;
    int rows = 10;
    int numberOfFloors = 3;

    ParkingModel[][][] parkingLot;

    DatabaseReference parkingLotRef;
    DatabaseReference myRef;

    // region constructors

    public ParkingLotModel() {
        parkingLotRef = FirebaseDatabase.getInstance().getReference("parking_lots").child(UID);

        initialize3DArray();
    }

    // endregion

    // region getters and setters

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public void setNumberOfFloors(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }

    public ParkingModel[][][] getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(ParkingModel[][][] parkingLot) {
        this.parkingLot = parkingLot;
    }
    // endregion

    // region initialize parking lot
    private void initialize3DArray() {
        parkingLot = new ParkingModel[numberOfFloors][rows][columns];

        for (int i = 0; i < numberOfFloors; i++) {
            myRef = parkingLotRef.child("floors").child(String.valueOf(i));
            parkingLot[i] = initialize2DArray();
        }
//        parkingLotRef.child("floors").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (int i = 0; i < snapshot.getChildrenCount(); i++) {
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private ParkingModel[][] initialize2DArray() {
        ParkingModel[][] floor = new ParkingModel[rows][columns];

/*       List<ParkingModel[]> parkingModels = new ArrayList<>();
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int myRows = (int) snapshot.getChildrenCount();
//                for(int i = 0; i < snapshot.getChildrenCount(); i++) {
//                    myRef.child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            ParkingModel[] array = new ParkingModel[(int) snapshot.getChildrenCount()];
//                            for(int j = 0; j < snapshot.getChildrenCount(); j++) {
//                                array[j] = snapshot.getValue(ParkingModel.class);
//                            }
//
//                            parkingModels.add(array);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
////                    floor = new ParkingModel[myRows][parkingModels.get(0).length];
//
//
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
      });
*/
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Log.d("Hello?", "hello?");
                final int x = i;
                final int y = j;
                myRef.child(String.valueOf(i)).child(String.valueOf(j)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ParkingModel parkingModel = snapshot.getValue(ParkingModel.class);
                        floor[x][y] = parkingModel;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        return floor;
    }
    // endregion
}
