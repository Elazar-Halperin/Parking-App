package com.example.parkingapp.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.parkingapp.ParkingActivity;
import com.example.parkingapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;
import java.util.Set;

public class SearchEndDialogFragment extends DialogFragment {

    Toolbar toolbar;

    Button btn_decline, btn_accept;
    TextView tv_floor, tv_row, tv_column;
    FloatingActionButton fab_closeDialog;

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

        btn_decline = dialog.findViewById(R.id.btn_decline);
        btn_accept = dialog.findViewById(R.id.btn_accept);

        tv_floor = dialog.findViewById(R.id.tv_floor);
        tv_row = dialog.findViewById(R.id.tv_row);
        tv_column = dialog.findViewById(R.id.tv_column);

        fab_closeDialog = dialog.findViewById(R.id.fab_closeDialog);

        fab_closeDialog.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btn_decline.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btn_accept.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), ParkingActivity.class);
            startActivity(i);
            dialog.dismiss();
        });


        return dialog;

    }
}
