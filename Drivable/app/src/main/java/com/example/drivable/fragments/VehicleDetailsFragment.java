package com.example.drivable.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.activities.AddVehicleActivity;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class VehicleDetailsFragment extends Fragment {

    private final String TAG = "VehicleDetsFragment.TAG";
    private VehicleDetailsFragmentListener vehicleDetailsFragmentListener;

    public static VehicleDetailsFragment newInstance() {

        Bundle args = new Bundle();

        VehicleDetailsFragment fragment = new VehicleDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface VehicleDetailsFragmentListener{
        Vehicle getVehicle();
        Account getAccount();
        void updateVehicle(Vehicle vehicleUpdate);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof VehicleDetailsFragmentListener){
            vehicleDetailsFragmentListener = (VehicleDetailsFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vehicle_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        setViewElements();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_vehicle_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals("Edit Vehicle")){
            //should open same add vehicle page but with values in the edit texts, spinner, and a different title
            Intent editVehicleIntent = new Intent(getContext(), AddVehicleActivity.class);
            editVehicleIntent.setAction(Intent.ACTION_RUN);
            editVehicleIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, vehicleDetailsFragmentListener.getAccount());
            editVehicleIntent.putExtra(IntentExtrasUtil.EXTRA_IS_EDITING, true);
            editVehicleIntent.putExtra(IntentExtrasUtil.EXTRA_VEHICLE, vehicleDetailsFragmentListener.getVehicle());

            editVehicleActivityLauncher.launch(editVehicleIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    private void setViewElements(){
        Vehicle selectedVehicle = vehicleDetailsFragmentListener.getVehicle();
        String acronym = vehicleDetailsFragmentListener.getAccount().getCompanyAcronym();

        //get elements
        TextView nameTV = getActivity().findViewById(R.id.vehicle_details_tv_name);
        TextView vinNumTV = getActivity().findViewById(R.id.vehicle_details_tv_vin_num);
        TextView odometerTV = getActivity().findViewById(R.id.vehicle_details_tv_odometer);
        TextView statusTV = getActivity().findViewById(R.id.vehicle_details_tv_status);
        TextView yearTV = getActivity().findViewById(R.id.vehicle_details_tv_year);
        TextView makeTV = getActivity().findViewById(R.id.vehicle_details_tv_make);
        TextView modelTV = getActivity().findViewById(R.id.vehicle_details_tv_model);
        TextView driveTrainTV = getActivity().findViewById(R.id.vehicle_details_tv_drive_train);
        ImageView makeImage = getActivity().findViewById(R.id.vehicle_details_iv_make_image);

        //set values to elements
        String vehicleName = acronym + "-" + selectedVehicle.getName();
        nameTV.setText(vehicleName);
        String vinString = "Vin #: " + selectedVehicle.getVinNum();
        vinNumTV.setText(vinString);
        String odometerString = "Odometer: " + selectedVehicle.getOdometer();
        odometerTV.setText(odometerString);
        if(selectedVehicle.isActive()){
            String active = "Active";
            statusTV.setText(active);
            statusTV.setTextColor(getResources().getColor(R.color.green));
        }
        else{
            String inactive = "Inactive";
            statusTV.setText(inactive);
            statusTV.setTextColor(getResources().getColor(R.color.red));
        }
        yearTV.setText(selectedVehicle.getYear());
        makeTV.setText(selectedVehicle.getMake());
        modelTV.setText(selectedVehicle.getModel());
        driveTrainTV.setText(selectedVehicle.getDriveTrain());

        String uri = "@drawable/" + selectedVehicle.getMake().toLowerCase();
        int imageResource = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
        makeImage.setImageResource(imageResource);
    }

    ActivityResultLauncher<Intent> editVehicleActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i(TAG, "onActivityResult: Result Code = " + result.getResultCode());

                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.i(TAG, "onActivityResult: Data found after selecting image");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS + "/" + vehicleDetailsFragmentListener.getAccount().getDocID() + "/" +
                                FirebaseUtil.COLLECTION_VEHICLES + "/").document(vehicleDetailsFragmentListener.getVehicle().getDocID()).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot doc) {

                                        String _name = doc.getString(FirebaseUtil.VEHICLES_FIELD_NAME);
                                        String _vinNum = doc.getString(FirebaseUtil.VEHICLES_FIELD_VIN_NUM);
                                        String _odometer = doc.getString(FirebaseUtil.VEHICLES_FIELD_ODOMETER);
                                        boolean _isActive = doc.getBoolean(FirebaseUtil.VEHICLES_FIELD_IS_ACTIVE);
                                        String _year = doc.getString(FirebaseUtil.VEHICLES_FIELD_YEAR);
                                        String _make = doc.getString(FirebaseUtil.VEHICLES_FIELD_MAKE);
                                        String _model = doc.getString(FirebaseUtil.VEHICLES_FIELD_MODEL);
                                        String _driveTrain = doc.getString(FirebaseUtil.VEHICLES_FIELD_DRIVE_TRAIN);

                                        Vehicle vehicleUpdate = new Vehicle(doc.getId(), _name, _vinNum, _odometer, _isActive, _year, _make, _model, _driveTrain);
                                        //vehicleDetailsFragmentListener.updateVehicle(vehicleUpdate);
                                        String acronym = vehicleDetailsFragmentListener.getAccount().getCompanyAcronym();

                                        //get elements
                                        TextView nameTV = getActivity().findViewById(R.id.vehicle_details_tv_name);
                                        TextView vinNumTV = getActivity().findViewById(R.id.vehicle_details_tv_vin_num);
                                        TextView odometerTV = getActivity().findViewById(R.id.vehicle_details_tv_odometer);
                                        TextView statusTV = getActivity().findViewById(R.id.vehicle_details_tv_status);
                                        TextView yearTV = getActivity().findViewById(R.id.vehicle_details_tv_year);
                                        TextView makeTV = getActivity().findViewById(R.id.vehicle_details_tv_make);
                                        TextView modelTV = getActivity().findViewById(R.id.vehicle_details_tv_model);
                                        TextView driveTrainTV = getActivity().findViewById(R.id.vehicle_details_tv_drive_train);
                                        ImageView makeImage = getActivity().findViewById(R.id.vehicle_details_iv_make_image);

                                        //set values to elements
                                        String vehicleName = acronym + "-" + vehicleUpdate.getName();
                                        nameTV.setText(vehicleName);
                                        String vinString = "Vin #: " + vehicleUpdate.getVinNum();
                                        vinNumTV.setText(vinString);
                                        String odometerString = "Odometer: " + vehicleUpdate.getOdometer();
                                        odometerTV.setText(odometerString);
                                        if(vehicleUpdate.isActive()){
                                            String active = "Active";
                                            statusTV.setText(active);
                                            statusTV.setTextColor(getResources().getColor(R.color.green));
                                        }
                                        else{
                                            String inactive = "Inactive";
                                            statusTV.setText(inactive);
                                            statusTV.setTextColor(getResources().getColor(R.color.red));
                                        }
                                        yearTV.setText(vehicleUpdate.getYear());
                                        makeTV.setText(vehicleUpdate.getMake());
                                        modelTV.setText(vehicleUpdate.getModel());
                                        driveTrainTV.setText(vehicleUpdate.getDriveTrain());

                                        String uri = "@drawable/" + vehicleUpdate.getMake().toLowerCase();
                                        int imageResource = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
                                        makeImage.setImageResource(imageResource);

                                    }
                                });
                    }
                    else {
                        Log.i(TAG, "onActivityResult: Error resetting data or Back button pressed");
                    }
                }
            }
    );

}