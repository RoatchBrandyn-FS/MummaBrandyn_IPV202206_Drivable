package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.MaintenanceLog;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.fragments.MLogsListFragment;
import com.example.drivable.fragments.VehicleDetailsFragment;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.channels.AcceptPendingException;
import java.util.ArrayList;

public class VehicleDetailsActivity extends AppCompatActivity implements VehicleDetailsFragment.VehicleDetailsFragmentListener, MLogsListFragment.MLogListFragmentListener {

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
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_baseline_home_32));
        }

        Intent currentIntent = getIntent();
        selectedVehicle = (Vehicle) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_VEHICLE);
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container, VehicleDetailsFragment.newInstance()).commit();

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container_logs, MLogsListFragment.newInstance()).commit();

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
    }

    @Override
    public void updateLogs(ArrayList<MaintenanceLog> logUpdate) {
        selectedVehicle.updateLogs(logUpdate);
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container_logs, MLogsListFragment.newInstance()).commit();
    }

    @Override
    public ArrayList<MaintenanceLog> getLogs() {
        return selectedVehicle.getLogs();
    }

    @Override
    public Vehicle getLogVehicle() {
        return selectedVehicle;
    }

    @Override
    public Account getLogAccount() {
        return userAccount;
    }
}
