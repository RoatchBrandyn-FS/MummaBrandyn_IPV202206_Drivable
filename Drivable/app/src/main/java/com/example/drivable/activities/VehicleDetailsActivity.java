package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivable.R;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.fragments.VehicleDetailsFragment;
import com.example.drivable.utilities.IntentExtrasUtil;

public class VehicleDetailsActivity extends AppCompatActivity implements VehicleDetailsFragment.VehicleDetailsFragmentListener {

    Vehicle selectedVehicle;

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

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container, VehicleDetailsFragment.newInstance()).commit();

    }

    @Override
    public Vehicle getVehicle() {
        return selectedVehicle;
    }
}
