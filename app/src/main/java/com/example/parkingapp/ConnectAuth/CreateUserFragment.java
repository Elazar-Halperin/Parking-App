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
import com.example.parkingapp.Models.UserModel;
import com.example.parkingapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextInputEditText et_userName, et_email, et_password;
    Button btn_returnToConnectFragment, btn_createAccount;

    FirebaseAuth mAuth;
    DatabaseReference userRef;

    LinearLayout ll_progressBar;

    public CreateUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateUserFragment newInstance(String param1, String param2) {
        CreateUserFragment fragment = new CreateUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_user, container, false);
        // Initialize all the views.
        btn_returnToConnectFragment = view.findViewById(R.id.btn_returnToAccountConnection);
        btn_createAccount = view.findViewById(R.id.btn_createAccount);

        et_userName = view.findViewById(R.id.et_nameCreate);
        et_email = view.findViewById(R.id.et_emailCreate);
        et_password = view.findViewById(R.id.et_passwordCreate);

        // gets the LinearLayout from the activity
        ll_progressBar = requireActivity().findViewById(R.id.ll_progressBar);

        // Initialize firebase authentication and database.
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        // when user clicks on the btn_createAccount button,
        // it will start the process of creating a new user.
        btn_createAccount.setOnClickListener(v -> {
            createAccount();
        });

        // when client clicks btn_returnToConnectFragment it will return him to connection fragment
        btn_returnToConnectFragment.setOnClickListener(v -> {
            // navigate to the createUserFragment via jetpack navigation tool!
            Navigation.findNavController(getActivity().findViewById(R.id.fragment))
                    .navigate(R.id.action_createUserFragment_to_connectUserFragment2);
        });

        return view;
    }

    private void createAccount() {
        // check the validation of the input
        // if the input is invalid then we want to stop the process.
        if (!isValidInput()) return;

        // here starts the creation user process
        // we want to hide all the others elements in the screen
        // and show a loading page.
        ll_progressBar.setVisibility(View.VISIBLE);

        // get all the needed fields.
        // use trim function to delete all the white spaces.
        String username = et_userName.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        // create the user in the authentication.
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // when it finishes it means the we are now connected
                        // so we want to write to our database a new user.
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        UserModel user = new UserModel(email, username);
                        // starting the process of writing to the database.
                        userRef.child(firebaseUser.getUid()).setValue(user)
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        // the process was successful
                                        // so now we quiting from the connectionActivity
                                        // and return to the mainActivity/page.
                                        ll_progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), "User Created successfully", Toast.LENGTH_SHORT).show();
                                        requireActivity().finish();
                                    } else {
                                        // in case the user created in the auth
                                        // but not in the database, delete the user from the auth.
                                        task2.getException().printStackTrace();
                                        ll_progressBar.setVisibility(View.GONE);
                                        firebaseUser.delete();
                                        Toast.makeText(getActivity(), "Error while creating the user, Pls try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        ll_progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Error while creating the user, Pls try again", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    /**
     * @return true, if the client's input is valid
     * checks the email, name and the password of the client.
     * if the client's input is invalid then it will return false value
     * and provide an error on the editTexts.
     */
    private boolean isValidInput() {
        // get all the text from the edit-texts and deleting
        // all the white spaces.
        String username = et_userName.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        // check if the user entered a text to all the fields
        // if not then provide an error.
        if (username.isEmpty()) {
            et_userName.setError("Full name is required!");
            et_userName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            et_email.setError("Email is required!");
            et_email.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            et_password.setError("Password is required!");
            et_password.requestFocus();
            return false;
        }

        // check if the email pattern is incorrect
        // if it is incorrect then provide an error.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please provide valid email");
            et_email.requestFocus();
            return false;
        }

        // check if the user's password is less then 6 characters
        // if it less then 6 then provide an error.
        if (password.length() < 6) {
            et_password.setError("Min password length is 6!");
            et_password.requestFocus();
            return false;
        }

        // All the fields are now correct with their values
        // so we can return true value, which means that the input is valid.
        return true;
    }
}