package com.example.drivable.acivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.fragments.SignInFragment;
import com.example.drivable.utilities.NetworkUtil;
import com.example.drivable.utilities.ToastUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity implements SignInFragment.SignInFragmentListener {

    private final String TAG = "SignInActivity.TAG";
    ArrayList<Account> accounts = new ArrayList<>();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(NetworkUtil.isConnected(this)){
            //fetchFirebaseAccounts();
        }
        else{
            ToastUtil.networkError(this);
        }


        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true).replace(R.id.fragment_container, SignInFragment.newInstance())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void signIn(String email, String password, Context context) {

        Log.i(TAG, "signIn: Email = " + email + ", password = " + password);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                ToastUtil.testResults(context);
                Log.i(TAG, "onSuccess: User Email = " + mAuth.getUid());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: Login Failed");
                Log.i(TAG, "onFailure: Error: " + e);
            }
        });
        
    }
}