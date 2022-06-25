package com.example.drivable.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.fragments.SignInFragment;
import com.example.drivable.utilities.FirebaseUtils;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.example.drivable.utilities.NetworkUtil;
import com.example.drivable.utilities.ToastUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity implements SignInFragment.SignInFragmentListener {

    private final String TAG = "SignInActivity.TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(!NetworkUtil.isConnected(this)){
            ToastUtil.networkError(this);
        }

        FirebaseUtils.mAuth = FirebaseAuth.getInstance();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true).replace(R.id.fragment_container, SignInFragment.newInstance())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseUtils.mAuth.getCurrentUser() != null){
            Log.i(TAG, "onStart: User already logged in");
        }
        
    }

    @Override
    public void signIn(String email, String password, Context context) {

        if(!NetworkUtil.isConnected(this)){
            ToastUtil.networkError(this);
        }
        else{

            Log.i(TAG, "signIn: Email = " + email + ", password = " + password);

            FirebaseUtils.mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Log.i(TAG, "onSuccess: User Email = " + FirebaseUtils.mAuth.getUid());

                    loadAccount(authResult.getUser().getUid(), context);

                    /*Intent dashboardIntent = new Intent(context, DashboardActivity.class);
                    dashboardIntent.setAction(Intent.ACTION_RUN);
                    dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(dashboardIntent);*/
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "onFailure: Login Failed");
                    Log.i(TAG, "onFailure: Error: " + e);
                    Log.i(TAG, "onFailure: " + FirebaseUtils.mAuth.getUid());
                }
            });
        }

    }


    private void loadAccount(String userID, Context context){

        final Account[] account = {null};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FirebaseUtils.COLLECTION_ACCOUNTS).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("FirebaseUtils.TAG", "onFailure: ");
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    String _userID = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_USERID);

                    if(_userID.equals(userID)){
                        String _accountImageRef = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_ACCOUNT_IMAGE_REF);
                        String _company = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_COMPANY);
                        String _companyAcronym = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_COMPANY_ACRONYM);
                        String _firstName = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_FIRST_NAME);
                        String _lastName = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_LAST_NAME);

                        account[0] = new Account(doc.getId(), _accountImageRef, _company, _companyAcronym, _firstName, _lastName);
                    }
                }

                Intent dashboardIntent = new Intent(context, DashboardActivity.class);
                dashboardIntent.setAction(Intent.ACTION_RUN);
                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                dashboardIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, account[0]);

                startActivity(dashboardIntent);
            }

        });

    }



}