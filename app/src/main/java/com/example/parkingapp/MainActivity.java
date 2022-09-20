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
import android.widget.Toast;

import com.example.parkingapp.ConnectAuth.ConnectAndCreateActivity;
import com.example.parkingapp.Fragments.SearchEndDialogFragment;
import com.example.parkingapp.Models.ParkingLotModel;
import com.example.parkingapp.Models.ParkingModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

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

    Button btn_connectUser, btn_searchUser;
    AutoCompleteTextView actv_choice;
    ParkingModel[][][] parkingLot;
    ParkingModel entrP;
    ParkingModel elevP;
    List<String> list;

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

        // sets
        Toast.makeText(getApplicationContext(), "mama the king", Toast.LENGTH_SHORT).show();

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
        btn_searchUser = findViewById(R.id.btn_searchParking);
        actv_choice = findViewById(R.id.actv_choices);

        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        sp = getSharedPreferences(PARKING_SPACE_KEY, Context.MODE_PRIVATE);

        if (user == null) {
            mAuth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    Log.d("hello", user.getUid());
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication denied", Toast.LENGTH_SHORT).show();
                }
            });
        }
        Log.d("hello", user.getUid());

        list = new ArrayList<>();
        list.add("Closest to the exit");
        list.add("Closest to the elevator");
        list.add("Disabled parking place");

        ArrayAdapter<? extends String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_item, list);

        actv_choice.setAdapter(adapter);
        actv_choice.setThreshold(0);

//        actv_choice.setText(list.get(1));actv_choice.setText(list.get(0));actv_choice.setText(list.get(2), false);

        // on click listener to search button
        // it will search for a place to park by the user preferences
        btn_searchUser.setOnClickListener(v -> {

            if (sp.getString(TAKEN_BY_KEY, "").equals(user.getUid())) {
                Intent i = new Intent(getApplicationContext(), ParkingActivity.class);
                startActivity(i);
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
                Toast.makeText(getApplicationContext(), "Try again...", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        });
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


    private void decideWhatButtonItShouldBe() {
        String connectivity = "";
        if (user == null || user.isAnonymous()) {
            connectivity = getString(R.string.connect);

            btn_connectUser.setOnClickListener(v -> {
                Intent i = new Intent(getApplicationContext(), ConnectAndCreateActivity.class);
                startActivity(i);
            });
        } else {
            connectivity = getString(R.string.disconnect);

            btn_connectUser.setOnClickListener(v -> {
                mAuth.signOut();
                // when we click sign out, we want to update the onClickListener behaviour,
                // so we do here to do that.
                decideWhatButtonItShouldBe();
            });
        }

        btn_connectUser.setText(connectivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sp.getString(TAKEN_BY_KEY, "").trim().equals(user.getUid().trim())) btn_searchUser.setText("Your parking space");

        decideWhatButtonItShouldBe();
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

        return searchForClosest("Closest to the elevator");

    }


    private ParkingModel searchForClosest(String uChoice) {
        ParkingModel[][][] parkingModels = lotModel.getParkingLot();
        ParkingModel startP = new ParkingModel();
        ParkingModel tempP = new ParkingModel();
        if (uChoice.equals("Closest to the entrance")) {
            startP = entrP;
        } else if (uChoice.equals("Closest to the elevator")) {
            startP = elevP;
        }

        //farest parking lot
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