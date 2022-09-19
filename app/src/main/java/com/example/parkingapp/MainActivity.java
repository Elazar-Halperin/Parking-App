package com.example.parkingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

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

import kotlinx.coroutines.internal.MainDispatcherFactory;

public class MainActivity extends AppCompatActivity {

    public static final String UID = "-NBwlYIL7hgoGjHg9P-D";

    Button btn_connectUser, btn_searchUser;
    AutoCompleteTextView actv_choice;
    ParkingModel[][][] parkingLot;

    DatabaseReference mainRef;
    FirebaseUser user;
    FirebaseAuth mAuth;

    ParkingLotModel parkingLotModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // disable night theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // sets the bar icons to dark color.
        toggleColorStatusBarIcons(MainActivity.this);

        // holy shit

        // sets
        Toast.makeText(getApplicationContext(), "mama the king", Toast.LENGTH_SHORT).show();


        parkingLotModel = new ParkingLotModel();


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
        actv_choice.setText(list.get(1));actv_choice.setText(list.get(0));actv_choice.setText(list.get(2));

        // on click listener to search button
        // it will search for a place to park by the user preferences
        btn_searchUser.setOnClickListener(v -> {
            parkingLot = parkingLotModel.getParkingLot();
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
        ParkingModel[][][] parkingLot = parkingLotModel.getParkingLot();

        ParkingModel current = null;

        for (int i = parkingLot.length - 1; i >= 0; i--) {
            for (int j = 1; j >= 0; j--) {
                for (int k = parkingLot[i][j].length - 1; k >= 0; k--) {
                    ParkingModel temp = parkingLot[i][j][k];
                    if (temp == null) {
                        continue;
                    }
                    if (temp.getStatus() == ParkingModel.NOT_TAKEN && temp.getType() == ParkingModel.HANDICAPPED) {
                        current = temp;
                    }
                }
            }
        }

        if (current != null) {

            return current;
        }

        return nearestToTheElevators();

    }

    private ParkingModel nearestToTheElevators() {
        for (int i = parkingLot.length - 1; i >= 0; i--) {
            for (int j = 1; j >= 0; j--) {
                for (int k = parkingLot[i][j].length - 1; k >= 0; k--) {
                    ParkingModel temp = parkingLot[i][j][k];
                    if (temp == null) {
                        continue;
                    }
                    if (temp.getStatus() == ParkingModel.NOT_TAKEN && temp.getType() == ParkingModel.HANDICAPPED) {

                    }
                }
            }
        }

        return null;
    }

}