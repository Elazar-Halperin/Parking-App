package com.example.parkingapp.ConnectAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import com.example.parkingapp.MainActivity;
import com.example.parkingapp.R;

public class ConnectAndCreateActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_and_create);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable return button to the parent activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // set the title of the toolbar to empty
        getSupportActionBar().setTitle("");
        // set the arrow drawable
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close_24px);

        // Adjust and resize the view when the keyboard opens.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}