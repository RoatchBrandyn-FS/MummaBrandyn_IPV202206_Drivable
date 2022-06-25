package com.example.drivable.fragments;

import android.content.Context;
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
import com.example.drivable.data_objects.Account;
import com.example.drivable.utilities.AlertsUtil;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.ToastUtil;
import com.example.drivable.utilities.ValidationUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "UpdateEmailFrag.TAG";
    UpdateEmailFragmentListener updateEmailFragmentListener;

    public static UpdateEmailFragment newInstance() {

        Bundle args = new Bundle();

        UpdateEmailFragment fragment = new UpdateEmailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface UpdateEmailFragmentListener{
        Account getAccount();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof UpdateEmailFragmentListener){
            updateEmailFragmentListener = (UpdateEmailFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get elements
        Button updateBtn = getActivity().findViewById(R.id.update_email_btn_update);
        updateBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.update_email_btn_update){

            //get edit texts
            EditText originalEmailET = getActivity().findViewById(R.id.update_email_et_original_email);
            EditText newEmailET = getActivity().findViewById(R.id.update_email_et_new_email);

            //get strings
            String originalEmailString = originalEmailET.getText().toString().trim();
            String newEmailString = newEmailET.getText().toString().trim();

            if(originalEmailString.isEmpty() || newEmailString.isEmpty()){
                AlertsUtil.emptyEmailsError(getContext());
            }
            else if (!originalEmailString.equals(FirebaseUtil.mAuth.getCurrentUser().getEmail())){
                AlertsUtil.emailMatchError(getContext());
            }
            else if (!ValidationUtil.isEmail(newEmailString)){
                AlertsUtil.emailError(getContext());
            }
            else if(originalEmailString.equals(newEmailString)){
                AlertsUtil.updateEmailMatchError(getContext());
            }
            else{
                updateEmail(newEmailString);
            }

        }
    }

    private void updateEmail(String newEmail){
        FirebaseUser user = FirebaseUtil.mAuth.getCurrentUser();

        user.updateEmail(newEmail).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthException){
                    Log.i(TAG, "onFailure: Error Code: " + ((FirebaseAuthException) e).getErrorCode());
                    if(((FirebaseAuthException) e).getErrorCode() == "ERROR_EMAIL_ALREADY_IN_USE"){
                        AlertsUtil.updateEmailDuplicateError(getContext());
                    }
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
