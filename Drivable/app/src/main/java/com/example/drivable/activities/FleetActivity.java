package com.example.drivable.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.MaintenanceLog;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_baseline_home_32));
        }
        
        //set fab button and image as button
        FloatingActionButton fab = findViewById(R.id.activity_fleet_fab);
        fab.setOnClickListener(this);
        ImageView qrIV = findViewById(R.id.activity_fleet_qr_button);
        qrIV.setOnClickListener(this);

        //set account data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        updateVehicles();

        searchArray = userAccount.getVehicles();
        //setSearchbar();

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case android.R.id.home:
                if(getParentActivityIntent() == null){
                    onBackPressed();
                }
                else{

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
    public void onClick(View view) {

        if(view.getId() == R.id.activity_fleet_fab){
            Log.i(TAG, "onClick: Should go to Add Vehicle Page");

            Intent addVehicleIntent = new Intent(this, AddVehicleActivity.class);
            addVehicleIntent.setAction(Intent.ACTION_RUN);
            addVehicleIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, userAccount);
            addVehicleIntent.putExtra(IntentExtrasUtil.EXTRA_IS_EDITING, false);

            addVehicleActivityLauncher.launch(addVehicleIntent);
            //need activity launcher for addVehicle
        }
        
        else if(view.getId() == R.id.activity_fleet_qr_button){
            Log.i(TAG, "onClick: QR pressed");

            ArrayList<Vehicle> vehicles = userAccount.getVehicles();
            Collections.sort(vehicles, new Comparator<Vehicle>() {
                @Override
                public int compare(Vehicle v1, Vehicle v2) {
                    return v1.getName().compareTo(v2.getName());
                }
            });

            Intent scannerIntent = new Intent(this, BarcodeScannerActivity.class);
            scannerIntent.setAction(Intent.ACTION_RUN);
            scannerIntent.putExtra(IntentExtrasUtil.EXTRA_LIST_VEHICLES, vehicles);

            barcodeScannerActivityLauncher.launch(scannerIntent);
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

    @Override
    public void updateVehicleEdits() {
        updateVehicles();
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
                            boolean _isAtLot = doc.getBoolean(FirebaseUtil.VEHICLES_FIELD_IS_AT_LOT);

                            Vehicle newVehicle = new Vehicle(doc.getId(), _name, _vinNum, _odometer, _isActive, _year, _make, _model, _driveTrain, _isAtLot);
                            _vehicles.add(newVehicle);

                            setMLogs(newVehicle);
                        }

                        userAccount.updateVehicles(_vehicles);
                        searchArray = userAccount.getVehicles();


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

    /*private void setSearchbar(){

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

    }*/

    private void setMLogs(Vehicle vehicle){

        ArrayList<MaintenanceLog> logs = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).document(userAccount.getDocID()).collection(FirebaseUtil.COLLECTION_VEHICLES).document(vehicle.getDocID())
                .collection(FirebaseUtil.COLLECTION_LOGS).get().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: Error Code: " + ((FirebaseFirestoreException) e).getCode());
                    }
                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots){

                            String logRefString = doc.getString(FirebaseUtil.LOGS_FIELD_REF);
                            String name = doc.getString(FirebaseUtil.LOGS_FIELD_NAME);
                            String date = doc.getString(FirebaseUtil.LOGS_FIELD_DATE);
                            String shopName = doc.getString(FirebaseUtil.LOGS_FIELD_SHOP_NAME);
                            String addressLine2 = doc.getString(FirebaseUtil.LOGS_FIELD_ADDRESS_LINE_2);
                            double lat = doc.getDouble(FirebaseUtil.LOGS_FIELD_LAT);
                            double lng = doc.getDouble(FirebaseUtil.LOGS_FIELD_LNG);
                            String report = doc.getString(FirebaseUtil.LOGS_FIELD_REPORT);

                            MaintenanceLog newLog = new MaintenanceLog(doc.getId(),name, date, logRefString, shopName, addressLine2, lat, lng, report);
                            logs.add(newLog);
                        }

                        vehicle.updateLogs(logs);

                        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                                .replace(R.id.activity_fleet_container_list, FleetListFragment.newInstance()).commit();
                    }
                });

    }

    ActivityResultLauncher<Intent> addVehicleActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == RESULT_OK){

                        updateVehicles();

                    }

                }
            });


            ActivityResultLauncher < Intent > barcodeScannerActivityLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            if (result.getResultCode() == RESULT_OK) {

                                Vehicle scannedVehicle = (Vehicle) result.getData().getSerializableExtra(IntentExtrasUtil.EXTRA_VEHICLE);

                                Log.i(TAG, "onActivityResult: Vehicle Name: " + scannedVehicle.getName());


                                Intent vehicleDetailsIntent = new Intent(getApplicationContext(), VehicleDetailsActivity.class);
                                vehicleDetailsIntent.setAction(Intent.ACTION_RUN);
                                vehicleDetailsIntent.putExtra(IntentExtrasUtil.EXTRA_VEHICLE, scannedVehicle);
                                vehicleDetailsIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, userAccount);

                                startActivity(vehicleDetailsIntent);

                            }

                        }
                    });

}
