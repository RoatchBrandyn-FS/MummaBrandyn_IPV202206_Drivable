package com.example.drivable.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.activities.SignInActivity;
import com.example.drivable.utilities.AlertsUtil;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.ToastUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "UpdatePasswordFrag.TAG";

    public static UpdatePasswordFragment newInstance() {

        Bundle args = new Bundle();

        UpdatePasswordFragment fragment = new UpdatePasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get elements
        Button updateBtn = getActivity().findViewById(R.id.update_password_btn_update);
        updateBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.update_password_btn_update){

            //get edit texts
            EditText newPasswordET = getActivity().findViewById(R.id.update_password_et_new_password);
            EditText confirmPasswordET = getActivity().findViewById(R.id.update_password_et_confirm_password);

            //get strings
            String newPasswordString = newPasswordET.getText().toString().trim();
            String confirmPasswordString = confirmPasswordET.getText().toString().trim();

            if(newPasswordString.isEmpty() || confirmPasswordString.isEmpty()){
                AlertsUtil.emptyPasswordsError(getContext());
            }
            else if (!newPasswordString.equals(confirmPasswordString)){
                AlertsUtil.passwordMatchError(getContext());
            }
            else if(newPasswordString.length() < 6){
                AlertsUtil.passwordMinError(getContext());
            }
            else{
                updatePassword(newPasswordString);
            }

        }
    }

    private void updatePassword(String newPassword){

        FirebaseUser user = FirebaseUtil.mAuth.getCurrentUser();

        user.updatePassword(newPassword).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthException){
                    Log.i(TAG, "onFailure: Error Code: " + ((FirebaseAuthException) e).getErrorCode());

                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent signInIntent = new Intent(getContext(), SignInActivity.class);
                signInIntent.setAction(Intent.ACTION_MAIN);
                signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                FirebaseUtil.mAuth.signOut();
                ToastUtil.accountUpdated(getContext());
                startActivity(signInIntent);
            }
        });

    }
}
