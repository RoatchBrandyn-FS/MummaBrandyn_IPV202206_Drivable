package com.example.drivable.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.activities.SignUpActivity;
import com.example.drivable.utilities.AlertsUtil;
import com.example.drivable.utilities.NetworkUtil;
import com.example.drivable.utilities.ToastUtil;

public class SignInFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "SignInFragment.TAG";
    private SignInFragmentListener signInFragmentListener;

    public static SignInFragment newInstance() {

        Bundle args = new Bundle();

        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface SignInFragmentListener{
        void signIn(String email, String password, Context context);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof SignInFragmentListener){
            signInFragmentListener = (SignInFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set buttons to click listeners
        Button signInBtn = getActivity().findViewById(R.id.sign_in_btn_sign_in);
        signInBtn.setOnClickListener(this);
        Button signUpBtn = getActivity().findViewById(R.id.sign_in_btn_signup);
        signUpBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.sign_in_btn_sign_in){

            if(!NetworkUtil.isConnected(getContext())){
                ToastUtil.networkError(getContext());
            }
            else{

                //get Edit Texts
                EditText emailET = getActivity().findViewById(R.id.sign_in_et_email);
                EditText passwordET = getActivity().findViewById(R.id.sign_in_et_password);

                //Convert Edit Text Values to String
                String emailString = emailET.getText().toString().trim();
                String passwordString = passwordET.getText().toString().trim();

                if(emailString.isEmpty() || passwordString.isEmpty()){
                    AlertsUtil.signInError(getContext());
                }
                else{
                    validateSignIn(emailString, passwordString, passwordET);
                }

            }


        }
        else if (view.getId() == R.id.sign_in_btn_signup){

            Intent signUpIntent = new Intent(getContext(), SignUpActivity.class);
            signUpIntent.setAction(Intent.ACTION_RUN);
            startActivity(signUpIntent);

        }

    }

    private void validateSignIn(String emailInput, String passwordInput, EditText passwordET){

        signInFragmentListener.signIn(emailInput, passwordInput, getContext());

        /*ArrayList<Account> accounts = signInFragmentListener.getAccounts();
        Account confirmedAccount = null;

        for(Account acnt: accounts){
            if(Objects.equals(acnt.getEmail(), emailInput)){
                confirmedAccount = acnt;
            }
        }

        if(confirmedAccount == null){
            AlertsUtil.emailMatchError(getContext());
            passwordET.setText("");
        }
        else{

            if(!confirmedAccount.getPassword().equals(passwordInput)){
                AlertsUtil.passwordSignInError(getContext());
                passwordET.setText("");
            }
            else{
                ToastUtil.testResults(getContext());
            }

        }*/

    }

}
