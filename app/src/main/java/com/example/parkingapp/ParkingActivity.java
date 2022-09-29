package com.example.parkingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.parkingapp.Models.ParkingLotModel;
import com.example.parkingapp.Models.ParkingModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;


public class ParkingActivity extends AppCompatActivity {

    public static final String ROW = "row";
    public static final String COLUMN = "column";
    public static final int DEFAULT_VALUE = -9;
    public static final String FLOOR = "floor";
    FloatingActionButton fab_closePark;

    LinearLayout ll_parkLeft, ll_parkMiddleFirst, ll_parkMiddleSecond, ll_parkRight;
    TextView tv_floor, tv_row, tv_column;
    List<ImageView> list_parkLeft, list_parkMiddleFirst, list_parkMiddleSecond, list_parkRight;

    DatabaseReference myRef;

    int floor;
    int row;
    int column;

    final String PARKING_UID = "-NBnHE5xam17oMrdi51X";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);


        fab_closePark = findViewById(R.id.fab_closeParking);

        tv_floor = findViewById(R.id.tv_showFloor);
        tv_row = findViewById(R.id.tv_showRow);
        tv_column = findViewById(R.id.tv_showColumn);

        ll_parkLeft = findViewById(R.id.ll_park0);
        ll_parkMiddleFirst = findViewById(R.id.ll_park1);
        ll_parkMiddleSecond = findViewById(R.id.ll_park2);
        ll_parkRight = findViewById(R.id.ll_park3);


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
        ImageView[][] matrix = new ImageView[9][4];
        for(int i = 8; i >= 0; i--) {
            matrix[i][0] = list_parkRight.get(matrix.length - i - 1);
            matrix[i][1] = list_parkMiddleSecond.get(matrix.length - i - 1);
            matrix[i][2] = list_parkMiddleFirst.get(matrix.length - i - 1);
            matrix[i][3] = list_parkLeft.get(matrix.length - i - 1);
        }

        SharedPreferences sp = getApplicationContext().getSharedPreferences(MainActivity.PARKING_SPACE_KEY, MODE_PRIVATE);

        Intent intent = getIntent();
        floor = intent.getIntExtra(FLOOR, DEFAULT_VALUE);
        row = intent.getIntExtra(ROW, DEFAULT_VALUE);
        column = intent.getIntExtra(COLUMN, DEFAULT_VALUE);

        // in case this is not a release option...
        if(row == DEFAULT_VALUE || column == DEFAULT_VALUE || floor == DEFAULT_VALUE) {
            floor = sp.getInt(MainActivity.FLOOR_KEY, 0);
            row = sp.getInt(MainActivity.ROW_KEY, 0);
            column = sp.getInt(MainActivity.COLUMN_KEY, 0);
        }

        tv_floor.setText("Floor: " + floor);
        tv_row.setText("Row: " + row);
        tv_column.setText("Column: " + column);

        fab_closePark.setOnClickListener( v -> {
            overridePendingTransition(0, 0);
            onBackPressed();

        });

        myRef = FirebaseDatabase.getInstance().getReference("parking_lots").child(ParkingLotModel.UID)
                .child("floors").child(String.valueOf(floor));
        for(int i = 0; i < ParkingModel.maxRow - 1; i++) {
            for(int j = 0; j < ParkingModel.maxColumn; j ++) {
                int finalI = i;
                int finalJ = j;
                myRef.child(String.valueOf(i)).child(String.valueOf(j))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    ParkingModel pm = snapshot.getValue(ParkingModel.class);
                                    if(pm.getStatus() == ParkingModel.TAKEN || pm.getStatus() == ParkingModel.RESERVED) {
                                        matrix[finalI][finalJ].setColorFilter(getResources().getColor(R.color.error_color));
                                        matrix[finalI][finalJ].setTranslationZ(10f);
                                    } else {
                                        matrix[finalI][finalJ].setColorFilter(getResources().getColor(R.color.white_color));
                                    }
                                    if(finalI == row && finalJ == column) {
                                        matrix[finalI][finalJ].setColorFilter(getResources().getColor(R.color.orange_color));
                                        matrix[finalI][finalJ].setTranslationZ(20f);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        }
    }
}