package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.fragments.FleetListFragment;
import com.example.drivable.fragments.ShopsListFragment;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ShopsActivity extends AppCompatActivity {

    private final String TAG = "ShopsActivity.TAG";
    Account userAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Fleet");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_baseline_home_32));
        }

        //set fab button
        FloatingActionButton fab = findViewById(R.id.activity_fleet_fab);
        //fab.setOnClickListener(this);

        //set account data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.activity_shops_container_list, ShopsListFragment.newInstance()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.i(TAG, "onOptionsItemSelected: item ID: " + item.getItemId());
        Log.i(TAG, "onOptionsItemSelected: button id: " + android.R.id.home);

        switch (item.getItemId()){
            case android.R.id.home:
                if(getParentActivityIntent() == null){
                    Log.i(TAG, "onOptionsItemSelected: Parent Activity not set in Manifest");
                    onBackPressed();
                }
                else{
                    Log.i(TAG, "onOptionsItemSelected: Should be sending to Dashboard");

                    Intent homeIntent = new Intent(this, DashboardActivity.class);
                    homeIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, userAccount);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    NavUtils.navigateUpTo(this, homeIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed: Something pressed");
    }
}
