package com.example.parkingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
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

    Button btn_connectUser, btn_searchUser;
    AutoCompleteTextView actv_choice;
    ParkingModel[][][] parkingLot;
    ParkingModel entrP;
    ParkingModel elevP;

    DatabaseReference mainRef;
    FirebaseUser user;
    FirebaseAuth mAuth;

    ParkingLotModel lotModel;

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

        List<String> list = new ArrayList<>();
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
            try {
                ParkingModel parkingModel;
                parkingLot = lotModel.getParkingLot();
                if(actv_choice.getText().toString().trim().equals("Disabled parking place")) {
                    parkingModel = getHandicappedParkingPlace();
                } else {
                    parkingModel = searchForClosest(actv_choice.getText().toString().trim());
                }

                Toast.makeText(getApplicationContext(), parkingModel.toString(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Couldn't find a parking place for you...", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            SearchEndDialogFragment dialog = new SearchEndDialogFragment();
            dialog.show(getSupportFragmentManager(), "myFragment");
        });
    }


    private void decideWhatButtonItShouldBe() {
        String connectivity = "";
        if(user == null) {
            connectivity = getString(R.string.connect);

            btn_connectUser.setOnClickListener(v-> {
                Intent i = new Intent(getApplicationContext(), ConnectAndCreateActivity.class);
                startActivity(i);
            });


        } else {
            connectivity = getString(R.string.disconnect);

            btn_connectUser.setOnClickListener(v -> {
//                mAuth.signOut();
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
        ParkingModel[][][]parkingLot = lotModel.getParkingLot();
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
        } else if (uChoice.equals("Closest to the elevator") ) {
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
                    if ((v.getStatus() == 0) && (startP.distance(tempP) > startP.distance(v))) {
                        tempP = v;
                    }
                }
            }
        }

        if(tempP.getStatus()== 0) return tempP;

        return new ParkingModel();

    }

}