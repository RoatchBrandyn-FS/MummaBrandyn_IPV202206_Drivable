package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.fragments.DashboardFragment;
import com.example.drivable.fragments.FleetListFragment;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FleetActivity extends AppCompatActivity implements FleetListFragment.FleetListFragmentListener, View.OnClickListener {

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
        
        //set fab button
        FloatingActionButton fab = findViewById(R.id.activity_fleet_fab);
        fab.setOnClickListener(this);

        //set account data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.activity_fleet_container_list, FleetListFragment.newInstance()).commit();

        searchArray = userAccount.getVehicles();
        setSearchbar();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateVehicles();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        dashboardIntent.setAction(Intent.ACTION_RUN);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        dashboardIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, userAccount);

        startActivity(dashboardIntent);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.activity_fleet_fab){
            Log.i(TAG, "onClick: Should go to Add Vehicle Page");

            Intent addVehicleIntent = new Intent(this, AddVehicleActivity.class);
            addVehicleIntent.setAction(Intent.ACTION_RUN);
            addVehicleIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, userAccount);
            addVehicleIntent.putExtra(IntentExtrasUtil.EXTRA_IS_EDITING, false);

            startActivity(addVehicleIntent);
        }
        
    }

    @Override
    public ArrayList<Vehicle> getVehiclesList() {
        return searchArray;
    }

    @Override
    public Account getAccount() {
        return userAccount;
    }

    public void updateVehicles() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).document(userAccount.getDocID()).collection(FirebaseUtil.COLLECTION_VEHICLES).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.i(TAG, "onSuccess: Vehicles Loaded Successfully");
                        //get values for vanTotals

                        ArrayList<Vehicle> _vehicles = new ArrayList<>();

                        //get values
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            Log.i(TAG, "onComplete: doc id = " + doc.getId());

                            String _name = doc.getString(FirebaseUtil.VEHICLES_FIELD_NAME);
                            String _vinNum = doc.getString(FirebaseUtil.VEHICLES_FIELD_VIN_NUM);
                            String _odometer = doc.getString(FirebaseUtil.VEHICLES_FIELD_ODOMETER);
                            boolean _isActive = doc.getBoolean(FirebaseUtil.VEHICLES_FIELD_IS_ACTIVE);
                            String _year = doc.getString(FirebaseUtil.VEHICLES_FIELD_YEAR);
                            String _make = doc.getString(FirebaseUtil.VEHICLES_FIELD_MAKE);
                            String _model = doc.getString(FirebaseUtil.VEHICLES_FIELD_MODEL);
                            String _driveTrain = doc.getString(FirebaseUtil.VEHICLES_FIELD_DRIVE_TRAIN);

                            Vehicle newVehicle = new Vehicle(doc.getId(), _name, _vinNum, _odometer, _isActive, _year, _make, _model, _driveTrain);
                            _vehicles.add(newVehicle);
                        }

                        userAccount.updateVehicles(_vehicles);
                        searchArray = userAccount.getVehicles();

                        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                                .replace(R.id.activity_fleet_container_list, FleetListFragment.newInstance()).commit();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseFirestoreException){
                            Log.i(TAG, "onFailure: Error Code: " + ((FirebaseFirestoreException) e).getCode());
                        }
                    }
                });

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
