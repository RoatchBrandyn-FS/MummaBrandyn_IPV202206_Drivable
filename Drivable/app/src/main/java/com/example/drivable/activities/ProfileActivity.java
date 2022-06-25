package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.fragments.ProfileFragment;
import com.example.drivable.utilities.IntentExtrasUtil;

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.ProfileFragmentListener {

    private final String TAG = "ProfileActivity.TAG";
    Account userAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Profile");
        }

        //set room and user data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container, ProfileFragment.newInstance()).commit();


    }

    @Override
    public Account getAccount() {
        return userAccount;
    }
}
