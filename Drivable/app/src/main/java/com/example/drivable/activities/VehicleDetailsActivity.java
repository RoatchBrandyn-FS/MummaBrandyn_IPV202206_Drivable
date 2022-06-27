package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.fragments.VehicleDetailsFragment;
import com.example.drivable.utilities.IntentExtrasUtil;

import java.nio.channels.AcceptPendingException;

public class VehicleDetailsActivity extends AppCompatActivity implements VehicleDetailsFragment.VehicleDetailsFragmentListener {

    Vehicle selectedVehicle;
    Account userAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Vehicle Details");
        }

        Intent currentIntent = getIntent();
        selectedVehicle = (Vehicle) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_VEHICLE);
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container, VehicleDetailsFragment.newInstance()).commit();

    }

    @Override
    public Vehicle getVehicle() {
        return selectedVehicle;
    }

    @Override
    public Account getAccount() {
        return userAccount;
    }

    @Override
    public void updateVehicle(Vehicle vehicleUpdate) {
        selectedVehicle = vehicleUpdate;
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container, VehicleDetailsFragment.newInstance()).commit();
    }
}
