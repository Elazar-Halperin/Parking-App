package com.example.parkingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.parkingapp.Models.ParkingLotModel;
import com.example.parkingapp.Models.ParkingModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ParkingActivity extends AppCompatActivity {

    FloatingActionButton fab_closePark;

    LinearLayout ll_parkLeft, ll_parkMiddleFirst, ll_parkMiddleSecond, ll_parkRight;
    List<ImageView> list_parkLeft, list_parkMiddleFirst, list_parkMiddleSecond, list_parkRight;

    ParkingLotModel parkingLotModel;
    ParkingModel [][][] ThreeDParkingArray;

    final String PARKING_UID = "-NBnHE5xam17oMrdi51X";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        fab_closePark = findViewById(R.id.fab_closeParking);

        ll_parkLeft = findViewById(R.id.ll_park0);
        ll_parkMiddleFirst = findViewById(R.id.ll_park1);
        ll_parkMiddleSecond = findViewById(R.id.ll_park2);
        ll_parkRight = findViewById(R.id.ll_park3);

        if(list_parkRight != null) return;

        Log.d("instance", "the instance still haven't been saves!");

        list_parkLeft = new ArrayList<>();
        list_parkRight = new ArrayList<>();
        list_parkMiddleSecond = new ArrayList<>();
        list_parkMiddleFirst = new ArrayList<>();

        for(int i = 0; i < ll_parkLeft.getChildCount(); i++) {
            list_parkLeft.add((ImageView) ll_parkLeft.getChildAt(i));
        }

        for(int i = 0; i < ll_parkRight.getChildCount(); i++) {
            list_parkRight.add((ImageView) ll_parkRight.getChildAt(i));
        }

        for(int i = 0; i < ll_parkMiddleFirst.getChildCount(); i++) {
            list_parkMiddleFirst.add((ImageView) ll_parkMiddleFirst.getChildAt(i));
        }

        for(int i = 0; i < ll_parkMiddleSecond.getChildCount(); i++) {
            list_parkMiddleSecond.add((ImageView) ll_parkMiddleSecond.getChildAt(i));
        }


        fab_closePark.setOnClickListener( v -> {
            overridePendingTransition(0, 0);
            onBackPressed();

        });
    }
}