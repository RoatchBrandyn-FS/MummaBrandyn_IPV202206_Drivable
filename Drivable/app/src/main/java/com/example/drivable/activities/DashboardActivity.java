package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.fragments.DashboardFragment;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DashboardActivity extends AppCompatActivity implements DashboardFragment.DashboardFragmentListener {

    private final String TAG = "DashboardActivity.TAG";
    Account userAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Dashboard");
        }

        //set room and user data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        if(FirebaseUtil.mAuth != null){
            Log.i(TAG, "onStart: User Signed In As: " + FirebaseUtil.mAuth.getUid());
        }

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container, DashboardFragment.newInstance()).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        updateUserAccount();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public Account getAccount() {
        Log.i(TAG, "getAccount: " + userAccount.getCompany());
        return userAccount;
    }

    @Override
    public void updateAccount(Account updatedAccount) {
        Log.i(TAG, "updateAccount: Updated");
        userAccount = updatedAccount;
    }

    private void updateUserAccount(){

        FirebaseUser user = FirebaseUtil.mAuth.getCurrentUser();
        final Account[] account = new Account[1];

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("FirebaseUtils.TAG", "onFailure: ");
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    String _userID = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_USERID);

                    if(_userID.equals(user.getUid())){
                        String _accountImageRef = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_ACCOUNT_IMAGE_REF);
                        String _company = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_COMPANY);
                        String _companyAcronym = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_COMPANY_ACRONYM);
                        String _firstName = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_FIRST_NAME);
                        String _lastName = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_LAST_NAME);

                        account[0] = new Account(doc.getId(), _accountImageRef, _company, _companyAcronym, _firstName, _lastName);
                        userAccount = account[0];
                    }
                }

            }

        });

    }


}
