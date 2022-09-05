package com.example.parkingapp.ConnectAuth;

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

import com.example.parkingapp.Models.UserModel;
import com.example.parkingapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        btn_returnToConnectFragment = view.findViewById(R.id.btn_returnToAccountConnection);
        btn_createAccount = view.findViewById(R.id.btn_createAccount);

        et_userName = view.findViewById(R.id.et_nameCreate);
        et_email = view.findViewById(R.id.et_emailCreate);
        et_password = view.findViewById(R.id.et_passwordCreate);

        ll_progressBar = getActivity().findViewById(R.id.ll_progressBar);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        btn_createAccount.setOnClickListener(v -> {
            createAccount();
        });

        btn_returnToConnectFragment.setOnClickListener(v -> {
            Navigation.findNavController(getActivity().findViewById(R.id.fragment))
                    .navigate(R.id.action_createUserFragment_to_connectUserFragment2);
        });

        return view;
    }

    private void createAccount() {
        if (!isValidInput()) return;

        ll_progressBar.setVisibility(View.VISIBLE);
        String username = et_userName.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    if (task.isSuccessful()) {
                        UserModel user = new UserModel(email, username);
                        userRef.child(firebaseUser.getUid()).setValue(user)
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        ll_progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), "User Created successfully", Toast.LENGTH_SHORT).show();
                                        getActivity().finish();
                                    } else {
                                        // in case the user created in the auth
                                        // but not in the database, delete the user from the auth.
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

    private boolean isValidInput() {
        // getting all the text from the edit-texts and deleting all the spaces at the end and the start
        String username = et_userName.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
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
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please provide valid email");
            et_email.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            et_password.setError("Password is required!");
            et_password.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            et_password.setError("Min password length is 6!");
            et_password.requestFocus();
            return false;
        }
        return true;
    }
}