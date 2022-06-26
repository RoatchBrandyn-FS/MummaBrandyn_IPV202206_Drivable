package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.fragments.DashboardFragment;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;

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
    public Account getAccount() {
        Log.i(TAG, "getAccount: " + userAccount.getCompany());
        return userAccount;
    }
}
