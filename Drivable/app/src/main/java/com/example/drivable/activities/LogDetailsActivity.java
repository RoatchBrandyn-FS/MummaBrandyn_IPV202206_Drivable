package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.MaintenanceLog;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.fragments.LogDetailsFragment;
import com.example.drivable.utilities.IntentExtrasUtil;

public class LogDetailsActivity extends AppCompatActivity implements LogDetailsFragment.LogDetailsFragmentListener {

    MaintenanceLog selectedLog;
    Vehicle selectedVehicle;
    Account userAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Log Details");
            actionBar.setHomeButtonEnabled(true);
        }

        Intent currentIntent = getIntent();
        selectedVehicle = (Vehicle) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_VEHICLE);
        selectedLog = (MaintenanceLog) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_LOG);
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container, LogDetailsFragment.newInstance()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public MaintenanceLog getLog() {
        return selectedLog;
    }

    @Override
    public Vehicle getVehicle() {
        return selectedVehicle;
    }

    @Override
    public Account getAccount() {
        return userAccount;
    }
}
