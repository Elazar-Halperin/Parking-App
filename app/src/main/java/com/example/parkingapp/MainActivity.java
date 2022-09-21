package com.example.parkingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkingapp.ConnectAuth.ConnectAndCreateActivity;
import com.example.parkingapp.Fragments.SearchEndDialogFragment;
import com.example.parkingapp.Models.ParkingLotModel;
import com.example.parkingapp.Models.ParkingModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String UID = "-NBwlYIL7hgoGjHg9P-D";
    public static final String PARKING_SPACE_KEY = "parkingSpace";

    public static final String FLOOR_KEY = "floor";
    public static final String ROW_KEY = "row";
    public static final String COLUMN_KEY = "column";
    public static final String STATUS_KEY = "status";
    public static final String TAKEN_BY_KEY = "takenBy";

    Button btn_connectUser, btn_searchParkingSpace;
    AutoCompleteTextView actv_choice;
    ParkingModel[][][] parkingLot;
    ParkingModel entrP;
    ParkingModel elevP;
    List<String> list;
    ArrayAdapter<String> adapter;

    FirebaseUser user;
    FirebaseAuth mAuth;

    ParkingLotModel lotModel;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // disable night theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // sets the bar icons to dark color.
        toggleColorStatusBarIcons(MainActivity.this);

        lotModel = new ParkingLotModel();

        entrP = new ParkingModel();
        entrP.setColumn(0);
        entrP.setRow(0);
        entrP.setFloor(0);

        elevP = new ParkingModel();
        elevP.setColumn(1);
        elevP.setRow(9);
        elevP.setFloor(0);



        btn_connectUser = findViewById(R.id.btn_auth);
        btn_searchParkingSpace = findViewById(R.id.btn_searchParking);
        actv_choice = findViewById(R.id.actv_choices);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        sp = getApplicationContext().getSharedPreferences(PARKING_SPACE_KEY, Context.MODE_PRIVATE);

        list = new ArrayList<>();
        list.add(getString(R.string.closest_to_the_entrance));
        list.add(getString(R.string.closest_to_the_elevator));
        list.add(getString(R.string.handicapped_parking_space));

        adapter = new ArrayAdapter<>(MainActivity.this,
                R.layout.spinner_item,new ArrayList<>(list));
        actv_choice.setAdapter(adapter);


        if (user == null) {
            mAuth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    Log.d("hello", user.getUid());

                } else {
                    btn_connectUser.setText(R.string.connect);
                    Toast.makeText(getApplicationContext(), "Authentication denied", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            btn_connectUser.setText(getString(R.string.disconnect));
        }
        mAuth.addAuthStateListener(firebaseAuth -> {

            if(firebaseAuth.getCurrentUser() == null || firebaseAuth.getCurrentUser().isAnonymous()) {
                if (firebaseAuth.getCurrentUser() == null) {
                    mAuth.signInAnonymously();
                }
                btn_connectUser.setText(getString(R.string.connect));
                btn_connectUser.setOnClickListener(v -> {
                    Intent i = new Intent(getApplicationContext(), ConnectAndCreateActivity.class);
                    startActivity(i);
//                    btn_connectUser.setText(getString(R.string.disconnect));
                });
            } else {
                btn_connectUser.setText(getString(R.string.disconnect));
                btn_connectUser.setOnClickListener(v -> {
                    mAuth.signOut();
                    mAuth.signInAnonymously().addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                        }
                    });

//                    btn_connectUser.setText(getString(R.string.connect));
                });
            }
        });

        btn_searchParkingSpace.setOnClickListener(v -> {

            if (sp.getString(TAKEN_BY_KEY, "").equals(user.getUid())) {
                Intent i = new Intent(getApplicationContext(), ParkingActivity.class);
                i.putExtra(ParkingActivity.ROW, sp.getInt(ROW_KEY, 0));
                i.putExtra(ParkingActivity.COLUMN, sp.getInt(COLUMN_KEY, 0));
                startActivity(i);
                updateFirebaseDatabase();

                SharedPreferences.Editor e = sp.edit();
                e.remove(FLOOR_KEY);
                e.remove(STATUS_KEY);
                e.remove(COLUMN_KEY);
                e.remove(ROW_KEY);
                e.remove(TAKEN_BY_KEY);
                e.apply();
                return;
            }

            ParkingModel parkingModel;
            try {
                parkingLot = lotModel.getParkingLot();
                if (actv_choice.getText().toString().trim().equals("Disabled parking place")) {
                    parkingModel = getHandicappedParkingPlace();
                } else {
                    parkingModel = searchForClosest(actv_choice.getText().toString().trim());
                }
                putValuesIntoSP(parkingModel);

                SearchEndDialogFragment dialog = new SearchEndDialogFragment();

                Bundle b = new Bundle();
                b.putSerializable(PARKING_SPACE_KEY, parkingModel);
                dialog.setArguments(b);
                dialog.show(getSupportFragmentManager(), "myFragment");
                Toast.makeText(getApplicationContext(), parkingModel.toString(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Parking lot is full...Try again later...", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        });

        btn_connectUser.setOnClickListener(v -> {
            if (user == null || user.isAnonymous()) {
                Intent i = new Intent(getApplicationContext(), ConnectAndCreateActivity.class);
                startActivity(i);
                user = mAuth.getCurrentUser();
            } else {
                mAuth.signOut();
                mAuth.signInAnonymously().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                    }
                });
            }
        });


//        actv_choice.setText(list.get(1));actv_choice.setText(list.get(0));actv_choice.setText(list.get(2), false);

        // on click listener to search button
        // it will search for a place to park by the user preferences

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sp.getString(TAKEN_BY_KEY, null) == null || sp.getString(TAKEN_BY_KEY, null).isEmpty()) {
            btn_searchParkingSpace.setText(R.string.search);
        } else {
            btn_searchParkingSpace.setText(R.string.release_parking_space);
        }
    }

    private void updateFirebaseDatabase() {
        DatabaseReference myRef = FirebaseDatabase.getInstance()
                .getReference("parking_lots").child(ParkingLotModel.UID)
                .child("floors").child(String.valueOf(sp.getInt(FLOOR_KEY, 0)))
                .child(String.valueOf(sp.getInt(ROW_KEY, 0)))
                .child(String.valueOf(sp.getInt(COLUMN_KEY, 0)));

        myRef.child(STATUS_KEY).setValue(ParkingModel.NOT_TAKEN);
        myRef.child(TAKEN_BY_KEY).setValue("");
    }

    private void putValuesIntoSP(ParkingModel parkingModel) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(FLOOR_KEY, parkingModel.getFloor());
        editor.putInt(ROW_KEY, parkingModel.getRow());
        editor.putInt(COLUMN_KEY, parkingModel.getColumn());
        editor.putString(TAKEN_BY_KEY, parkingModel.getTakenBy());
        editor.putInt(STATUS_KEY, parkingModel.getStatus());

        editor.apply();
    }

    private void toggleColorStatusBarIcons(Activity activity) {
        // check if the sdk version supports the colored icon bar.
        if (Build.VERSION.SDK_INT >= 23) {
            View decor = activity.getWindow().getDecorView();
            // dark color icons
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private ParkingModel getHandicappedParkingPlace() {
        ParkingModel[][][] parkingLot = lotModel.getParkingLot();
        ParkingModel current = null;

        Log.d("wassup", parkingLot[0][0][0].toString());

        for (int i = parkingLot.length - 1; i >= 0; i--) {
            for (int j = parkingLot[i].length - 2; j < parkingLot[i].length - 1; j++) {
                for (int k = parkingLot[i][j].length - 1; k >= 0; k--) {
                    ParkingModel temp = parkingLot[i][j][k];
                    if (temp.getStatus() == ParkingModel.NOT_TAKEN && temp.getType() == ParkingModel.HANDICAPPED) {
                        current = temp;

                    }
                }
            }
        }

        if (current != null) {

            return current;
        }

        return searchForClosest(getString(R.string.closest_to_the_elevator));

    }


    private ParkingModel searchForClosest(String uChoice) {
        ParkingModel[][][] parkingModels = lotModel.getParkingLot();
        ParkingModel startP = new ParkingModel();
        ParkingModel tempP = new ParkingModel();
        if (uChoice.equals(getString(R.string.closest_to_the_entrance))) {
            startP = entrP;
        } else if (uChoice.equals(getString(R.string.closest_to_the_elevator))) {
            startP = elevP;
        }

        tempP.setFloor(lotModel.getNumberOfFloors() - 1);
        tempP.setRow(lotModel.getRows() - 2);
        tempP.setColumn(lotModel.getColumns() - 1);

        for (int k = 0; k < lotModel.getNumberOfFloors(); k++) {
            for (int i = 0; i < lotModel.getRows() - 1; i++) {
                for (int j = 0; j < lotModel.getColumns(); j++) {
                    ParkingModel v = parkingModels[k][i][j];
                    if (v.getType() == ParkingModel.USUAL && (v.getStatus() == ParkingModel.NOT_TAKEN) && (startP.distance(tempP) > startP.distance(v))) {
                        tempP = v;
                    }
                }
            }
        }

        if (tempP.getStatus() == 0) return tempP;
        return new ParkingModel();
    }

}