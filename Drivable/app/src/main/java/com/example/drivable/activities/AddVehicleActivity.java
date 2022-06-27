package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.fragments.AddVehicleFragment;
import com.example.drivable.utilities.IntentExtrasUtil;

public class AddVehicleActivity extends AppCompatActivity implements AddVehicleFragment.AddVehicleFragmentListener {

    private final String TAG = "AddVehicleActvty.TAG";
    Account userAccount;
    Vehicle selectedVehicle;
    boolean isEditing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("New Vehicle");
        }

        //set account data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);
        isEditing = currentIntent.getBooleanExtra(IntentExtrasUtil.EXTRA_IS_EDITING, false);

        if(isEditing){
            selectedVehicle = (Vehicle) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_VEHICLE);

            if(actionBar != null){
                actionBar.setTitle("Edit Vehicle");
            }
        }

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.fragment_container, AddVehicleFragment.newInstance()).commit();


    }

    @Override
    public Account getAccount() {
        return userAccount;
    }

    @Override
    public boolean isEditing() {
        return isEditing;
    }

    @Override
    public Vehicle getVehicle() {
        return selectedVehicle;
    }

}
