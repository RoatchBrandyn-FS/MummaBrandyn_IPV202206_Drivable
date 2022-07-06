package com.example.drivable.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.drivable.R;
import com.example.drivable.activities.VehicleDetailsActivity;
import com.example.drivable.adapters.VehicleAdapter;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.MaintenanceLog;
import com.example.drivable.data_objects.Vehicle;
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
import java.util.Collections;
import java.util.Comparator;

public class FleetListFragment extends ListFragment {

    private final String TAG = "FleetListFragment.TAG";
    private FleetListFragmentListener fleetListFragmentListener;

    public static FleetListFragment newInstance() {

        Bundle args = new Bundle();

        FleetListFragment fragment = new FleetListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface FleetListFragmentListener{
        ArrayList<Vehicle> getVehiclesList();
        Account getAccount();
        void updateVehicleEdits();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FleetListFragmentListener){
            fleetListFragmentListener = (FleetListFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_fleet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setFleetList();


    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ArrayList<Vehicle> vehicles = fleetListFragmentListener.getVehiclesList();
        Collections.sort(vehicles, new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle v1, Vehicle v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });

        Intent vehicleDetailsIntent = new Intent(getContext(), VehicleDetailsActivity.class);
        vehicleDetailsIntent.setAction(Intent.ACTION_RUN);
        vehicleDetailsIntent.putExtra(IntentExtrasUtil.EXTRA_VEHICLE, vehicles.get(position));
        vehicleDetailsIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, fleetListFragmentListener.getAccount());

        vehicleDetailsActivityLauncher.launch(vehicleDetailsIntent);
        //need activity launcher for vehicleDetails page
    }

    private void setFleetList(){

        ArrayList<Vehicle> vehicles = fleetListFragmentListener.getVehiclesList();
        Collections.sort(vehicles, new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle v1, Vehicle v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });
        String acronym = fleetListFragmentListener.getAccount().getCompanyAcronym();
        VehicleAdapter vehicleAdapter = new VehicleAdapter(getContext(), vehicles, acronym);

        setListAdapter(vehicleAdapter);
    }

    ActivityResultLauncher<Intent> vehicleDetailsActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    fleetListFragmentListener.updateVehicleEdits();

                }
            });

}
