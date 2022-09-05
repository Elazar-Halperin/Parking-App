package com.example.parkingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentContainerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.parkingapp.Adapters.CustomSpinner;
import com.example.parkingapp.Adapters.SpinnerChoiceAdapter;
import com.example.parkingapp.ConnectAuth.ConnectAndCreateActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomSpinner.OnSpinnerEventsListener {

    CustomSpinner spinner;
    SpinnerChoiceAdapter adapter;
    Button btn_connectUser, btn_searchUser;
    FragmentContainerView fmg_parking;
    FirebaseUser user;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // disable night theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // sets the bar icons to dark color.
        toggleColorStatusBarIcons(MainActivity.this);

        spinner = findViewById(R.id.spinner);
        btn_connectUser = findViewById(R.id.btn_auth);
        btn_searchUser = findViewById(R.id.btn_searchParking);
        mAuth = FirebaseAuth.getInstance();

        fmg_parking = findViewById(R.id.fmg_parkingLot);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the user is authenticated, if he does then set the text to connected
        // without any on click listener
        // if he doesn't then set on click listener which will pass him to authentication activity.
        if (user != null) {
            btn_connectUser.setText(R.string.connected);
        } else {
            btn_connectUser.setText(R.string.connect);
            btn_connectUser.setOnClickListener(v -> {
                Intent i = new Intent(getApplicationContext(), ConnectAndCreateActivity.class);
                startActivity(i);
            });
        }

        List<String> list = new ArrayList<>();
        list.add("Closest to the exit");
        list.add("Closest to the elevator");
        list.add("Disabled parking place");
        list.add("Cheapest");
        list.add("The most expensive");

        spinner.setSpinnerEventsListener(this);

        adapter = new SpinnerChoiceAdapter(getApplicationContext(), list);

        spinner.setAdapter(adapter);
        spinner.setDropDownVerticalOffset(140);

        // if user click on the fragment then it will make the illusion of full screen.
        fmg_parking.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), ParkingActivity.class);

//            Pair [] pairs = {
//                    new Pair<View, String>(fmg_parking, "parkingTransition")
//            };
//
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);

//            startActivity(i, options.toBundle());
            startActivity(i);
//            overridePendingTransition(0, 0);

        });


        // on click listener to search button
        // it will search for a place to park by the user preferences
        btn_searchUser.setOnClickListener(v -> {

            mAuth.signOut();
            btn_connectUser.setText("Connect");
            btn_connectUser.setOnClickListener(v1 -> {
                Intent i = new Intent(getApplicationContext(), ConnectAndCreateActivity.class);
                startActivity(i);

            });
        });
    }

    @Override
    public void onPopupWindowOpened(Spinner s) {
        spinner.setBackground(getDrawable(R.drawable.bg_spinner_up));
    }

    @Override
    public void onPopupWindowClosed(Spinner s) {
        spinner.setBackground(getDrawable(R.drawable.bg_spinner));
    }

    private void toggleColorStatusBarIcons(Activity activity) {
        // check if the sdk version supports the colored icon bar.
        if (Build.VERSION.SDK_INT >= 23) {
            View decor = activity.getWindow().getDecorView();
            // dark color icons
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
}