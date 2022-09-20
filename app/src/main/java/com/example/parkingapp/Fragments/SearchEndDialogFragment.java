package com.example.parkingapp.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.parkingapp.MainActivity;
import com.example.parkingapp.Models.ParkingLotModel;
import com.example.parkingapp.Models.ParkingModel;
import com.example.parkingapp.ParkingActivity;
import com.example.parkingapp.R;
import com.google.android.gms.common.internal.StringResourceValueReader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

public class SearchEndDialogFragment extends DialogFragment {

    Button btn_decline, btn_accept;
    TextView tv_floor, tv_row, tv_column;
    FloatingActionButton fab_closeDialog;
    FirebaseUser user;
    DatabaseReference parkingLotRef;

    SharedPreferences sp;

    int floor;
    int row;
    int column;


    ParkingModel parkingModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.CustomDatePickerDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fragment_search_end);
        dialog.setTitle("Elazar THe king|");

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(params);

        sp = getActivity().getSharedPreferences(MainActivity.PARKING_SPACE_KEY, Context.MODE_PRIVATE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        parkingLotRef = FirebaseDatabase.getInstance().getReference("parking_lots").child(ParkingLotModel.UID).child("floors");

        btn_decline = dialog.findViewById(R.id.btn_decline);
        btn_accept = dialog.findViewById(R.id.btn_accept);

        tv_floor = dialog.findViewById(R.id.tv_floor);
        tv_row = dialog.findViewById(R.id.tv_row);
        tv_column = dialog.findViewById(R.id.tv_column);

        floor = sp.getInt(MainActivity.FLOOR_KEY, 0);
        row = sp.getInt(MainActivity.ROW_KEY, 0);
        column = sp.getInt(MainActivity.COLUMN_KEY, 0);

        fab_closeDialog = dialog.findViewById(R.id.fab_closeDialog);

        tv_floor.setText("Floor: " + floor);
        tv_row.setText("Row: " + row);
        tv_column.setText("Column: " + column);


        updateStatus(ParkingModel.RESERVED);

        fab_closeDialog.setOnClickListener(v -> {
            dialog.dismiss();
            updateStatus(ParkingModel.NOT_TAKEN);
        });

        btn_decline.setOnClickListener(v -> {
            dialog.dismiss();
//            parkingModel.setStatus(ParkingModel.NOT_TAKEN);
//            updateParkingSpace(parkingModel);
            updateStatus(ParkingModel.NOT_TAKEN);
        });

        btn_accept.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), ParkingActivity.class);
//            parkingModel.setStatus(ParkingModel.TAKEN);
//            parkingModel.setTakenBy(user.getUid());

//            updateParkingSpace(parkingModel);
            updateStatus(ParkingModel.TAKEN);
            updateTakenBy(user.getUid());
            startActivity(i);
            dialog.dismiss();
        });


        return dialog;
    }

    private void updateStatus(int s) {
        parkingLotRef.child(String.valueOf(floor)).child(String.valueOf(row)).child(String.valueOf(column))
                .child(MainActivity.STATUS_KEY).setValue(s);

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(MainActivity.STATUS_KEY, s);
        editor.apply();
    }

    private void updateTakenBy(String s) {
        parkingLotRef.child(String.valueOf(floor)).child(String.valueOf(row)).child(String.valueOf(column))
                .child(MainActivity.TAKEN_BY_KEY).setValue(s);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MainActivity.TAKEN_BY_KEY, s);
        editor.apply();
    }
}