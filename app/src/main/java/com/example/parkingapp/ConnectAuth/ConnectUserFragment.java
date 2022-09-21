package com.example.parkingapp.ConnectAuth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.parkingapp.MainActivity;
import com.example.parkingapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ConnectUserFragment extends Fragment {

    Button btn_createUser, btn_connect;
    TextInputEditText et_email, et_password;
    LinearLayout ll_progressBar;

    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect_user, container, false);

        // Get all the wanted views from the fragment
        btn_createUser = view.findViewById(R.id.btn_createAccountIntent);
        btn_connect = view.findViewById(R.id.btn_connect);
        et_email = view.findViewById(R.id.et_emailConnect);
        et_password = view.findViewById(R.id.et_passwordConnect);

        // get the linearLayout with the progress bar, for when the user tries to connect.
        ll_progressBar = getActivity().findViewById(R.id.ll_progressBar);

        mAuth = FirebaseAuth.getInstance();


        btn_connect.setOnClickListener(v -> {
            // connect the user
            connect();
        });


        btn_createUser.setOnClickListener(v -> {
            Navigation.findNavController(getActivity().findViewById(R.id.fragment))
                    .navigate(R.id.action_connectUserFragment_to_createUserFragment);
        });

        return view;
    }

    private void connect() {
        // if the input isn't valid then get out of the function
        if(!isValidInput()) return;

        // get the string input and then use the trim function to delete all the extra spaces.
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        // make the progress bar visible
        ll_progressBar.setVisibility(View.VISIBLE);
        // sign in the user.
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                // when the task is finished the go back into the MainActivity
                Toast.makeText(getActivity(), "Connected successfully", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            } else {
                Toast.makeText(getActivity(), "Failed to connect!", Toast.LENGTH_SHORT).show();
            }
            ll_progressBar.setVisibility(View.GONE);
        });
    }


    public boolean isValidInput() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if(email.isEmpty()) {
            et_email.setError("Email is required!");
            et_email.requestFocus();
            return false;
        }
        if(password.isEmpty()) {
            et_password.setError("Password is required!");
            et_password.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please provide valid email");
            et_email.requestFocus();
            return false;
        }
        if(password.length() < 6) {
            et_password.setError("Min password length 6");
            et_password.requestFocus();
            return false;
        }
        return true;
    }

}