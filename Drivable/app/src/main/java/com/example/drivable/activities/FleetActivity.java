package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.fragments.DashboardFragment;
import com.example.drivable.fragments.FleetListFragment;
import com.example.drivable.utilities.IntentExtrasUtil;

import java.util.ArrayList;

public class FleetActivity extends AppCompatActivity implements FleetListFragment.FleetListFragmentListener{

    private final String TAG = "FleetActivity.TAG";
    Account userAccount;
    ArrayList<Vehicle> searchArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Fleet");
        }

        //set room and user data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.activity_fleet_container_list, FleetListFragment.newInstance()).commit();

        searchArray = userAccount.getVehicles();
        setSearchbar();
    }

    @Override
    public ArrayList<Vehicle> getVehiclesList() {
        return searchArray;
    }

    @Override
    public String getRoomDocID() {
        return null;
    }

    private void setSearchbar(){

        EditText searchET = findViewById(R.id.activity_fleet_searchbar);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "beforeTextChanged: " + charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "onTextChanged: " + charSequence);
                //do something here
                searchArray = new ArrayList<>();

                if(charSequence.equals("")){
                    searchArray = userAccount.getVehicles();
                }
                else{
                    for (Vehicle v: userAccount.getVehicles()){
                        if(v.getName().toLowerCase().contains(charSequence) || v.getVinNum().toLowerCase().contains(charSequence)){
                            searchArray.add(v);
                        }
                    }
                }


                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                        .replace(R.id.activity_fleet_container_list, FleetListFragment.newInstance()).commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged: " + editable);
            }
        });

    }
}
