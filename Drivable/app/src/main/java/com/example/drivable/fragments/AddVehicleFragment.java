package com.example.drivable.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.MaintenanceLog;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.utilities.AlertsUtil;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.ToastUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.net.InternetDomainName;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddVehicleFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "AddVehicleFrag.TAG";
    private AddVehicleFragmentListener addVehicleFragmentListener;

    public static AddVehicleFragment newInstance() {

        Bundle args = new Bundle();

        AddVehicleFragment fragment = new AddVehicleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface AddVehicleFragmentListener{
        Account getAccount();
        boolean isEditing();
        Vehicle getVehicle();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof AddVehicleFragmentListener){
            addVehicleFragmentListener = (AddVehicleFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_vehicle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set button click events
        Button vehicleBtn = getActivity().findViewById(R.id.new_vehicle_btn);
        vehicleBtn.setOnClickListener(this);

        //set Vehicle Name and spinner
        setVehicleName();
        setSpinner();

        //check for if editing
        if(addVehicleFragmentListener.isEditing()){
            setElementValues();
        }

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.new_vehicle_btn){
            Log.i(TAG, "onClick: Do error handling before adding/return to previous activity");

            validateElements();

        }

    }

    private void setVehicleName(){

        //get elements
        Account account = addVehicleFragmentListener.getAccount();
        Vehicle vehicle = addVehicleFragmentListener.getVehicle();
        TextView nameTV = getActivity().findViewById(R.id.new_vehicle_tv_name);

        //create dynamic naming
        String nameString = "";

        if(!addVehicleFragmentListener.isEditing()){
            //isEditing false
            if(account.getVehicles().size() < 9){
                nameString = "Name: " + account.getCompanyAcronym() + "-0" + (account.getVehicles().size() + 1);
            }
            else {
                nameString = "Name: " + account.getCompanyAcronym() + "-" + (account.getVehicles().size() + 1);
            }
        }
        else{
            //isEditing true
            nameString = "Name: " + account.getCompanyAcronym() + "-" + vehicle.getName();
        }


        nameTV.setText(nameString);
    }

    private void setSpinner(){

        Spinner makeSpinner = getActivity().findViewById(R.id.add_vehicle_spinner_make);
        String[] makeList = getResources().getStringArray(R.array.vehicle_types);

        ArrayAdapter<String> makeSpinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, makeList);
        makeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        makeSpinner.setAdapter(makeSpinnerAdapter);

    }

    private void setElementValues(){
        //get elements
        Vehicle vehicle = addVehicleFragmentListener.getVehicle();
        TextView titleTV = getActivity().findViewById(R.id.new_vehicle_tv_title);
        EditText yearET = getActivity().findViewById(R.id.new_vehicle_et_year);
        Spinner makeSpinner = getActivity().findViewById(R.id.add_vehicle_spinner_make);
        EditText modelET = getActivity().findViewById(R.id.new_vehicle_et_model);
        EditText driveTrainET = getActivity().findViewById(R.id.new_vehicle_et_drive_train);
        EditText vinNumET = getActivity().findViewById(R.id.new_vehicle_et_vin_num);
        EditText odometerET = getActivity().findViewById(R.id.new_vehicle_et_odometer);
        SwitchCompat activeSwitch = getActivity().findViewById(R.id.new_vehicle_switch);
        Button vehicleBtn = getActivity().findViewById(R.id.new_vehicle_btn);

        //set elements with values
        String newTitle = "Edit Vehicle";
        titleTV.setText(newTitle);

        yearET.setText(vehicle.getYear());

        switch (vehicle.getMake()){
            case "Dodge":
                makeSpinner.setSelection(1);
                break;
            case "Ford":
                makeSpinner.setSelection(2);
                break;
            case "Mercedes":
                makeSpinner.setSelection(3);
                break;
            case "Nissan":
                makeSpinner.setSelection(4);
                break;
        }

        modelET.setText(vehicle.getModel());
        driveTrainET.setText(vehicle.getDriveTrain());
        vinNumET.setText(vehicle.getVinNum());
        odometerET.setText(vehicle.getOdometer());
        activeSwitch.setChecked(vehicle.isActive());

        String newButton = "Update";
        vehicleBtn.setText(newButton);

    }

    private void validateElements(){

        //get elements
        TextView nameTV = getActivity().findViewById(R.id.new_vehicle_tv_name);
        EditText yearET = getActivity().findViewById(R.id.new_vehicle_et_year);
        Spinner makeSpinner = getActivity().findViewById(R.id.add_vehicle_spinner_make);
        EditText modelET = getActivity().findViewById(R.id.new_vehicle_et_model);
        EditText driveTrainET = getActivity().findViewById(R.id.new_vehicle_et_drive_train);
        EditText vinNumET = getActivity().findViewById(R.id.new_vehicle_et_vin_num);
        EditText odometerET = getActivity().findViewById(R.id.new_vehicle_et_odometer);
        SwitchCompat activeSwitch = getActivity().findViewById(R.id.new_vehicle_switch);

        //get strings
        String nameString = nameTV.getText().toString();
        String yearString = yearET.getText().toString().trim();
        String makeString = makeSpinner.getSelectedItem().toString();
        String modelString = modelET.getText().toString().trim();
        String driveTrainString = driveTrainET.getText().toString().trim();
        String vinNumString = vinNumET.getText().toString().trim();
        String odometerString = odometerET.getText().toString().trim();
        //switch just use switch.isChecked()
        Log.i(TAG, "validateElements: Spinner: " + makeString);

        if(nameString.isEmpty() || yearString.isEmpty() || makeString.isEmpty() || modelString.isEmpty() || driveTrainString.isEmpty() ||
        vinNumString.isEmpty() || odometerString.isEmpty()){

            if(!addVehicleFragmentListener.isEditing()){
                //isEditing false
                AlertsUtil.addVehicleAddEmptyError(getContext());
            }
            else{
                //isEditing true
                AlertsUtil.addVehicleUpdateEmptyError(getContext());
            }

        }
        else if(makeString.equals("Make")){
            AlertsUtil.addVehicleChooseMakeError(getContext());
        }
        else if(yearString.length() < 4 ){
            AlertsUtil.addVehicleYearLengthError(getContext());
        }
        else if(vinNumString.length() < 17){
            AlertsUtil.addVehicleVinLengthError(getContext());
        }
        else if( yearString.equals("NA") || makeString.equals("NOT ASSIGNED") || modelString.equals("NOT ASSIGNED") || driveTrainString.equals("NOT ASSIGNED")
                || vinNumString.equals("NOT ASSIGNED") || odometerString.equals("NA")){
            AlertsUtil.addVehicleReAssign(getContext());
        }
        else{


            if(addVehicleFragmentListener.isEditing()){
                //update current vehicle doc
                updateVehicle(yearString, makeString, modelString, driveTrainString, vinNumString, odometerString, activeSwitch.isChecked());
            }
            else{
                //save data to firestore
                String[] nameSplit1 = nameString.split(":");
                String[] nameSplit2 = nameSplit1[1].trim().split("-");

                addVehicle(nameSplit2[1].trim(), yearString, makeString, modelString, driveTrainString, vinNumString, odometerString, activeSwitch.isChecked());
            }

        }

    }

    private void addVehicle(String _name, String _year, String _make, String _model,  String _driveTrain, String _vinNum, String odometer, boolean _isActive){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Account account = addVehicleFragmentListener.getAccount();

        Map<String, Object> vehicle = new HashMap<>();
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_NAME, _name);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_YEAR, _year);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_MAKE, _make);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_MODEL, _model);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_DRIVE_TRAIN, _driveTrain);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_VIN_NUM, _vinNum);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_ODOMETER, odometer);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_IS_ACTIVE, _isActive);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_IS_AT_LOT, true);

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS + "/" + account.getDocID() + "/" + FirebaseUtil.COLLECTION_VEHICLES).add(vehicle)
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseFirestoreException){
                    Log.i(TAG, "onFailure: Error Code: " + ((FirebaseFirestoreException) e).getCode());
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        ToastUtil.vehicleAdded(getContext());

                        Intent resultIntent = new Intent();
                        getActivity().setResult(Activity.RESULT_OK, resultIntent);

                        getActivity().finish();
                    }
                });

    }

    private void updateVehicle(String _year, String _make, String _model,  String _driveTrain, String _vinNum, String odometer, boolean _isActive){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Account account = addVehicleFragmentListener.getAccount();
        String vehicleID = addVehicleFragmentListener.getVehicle().getDocID();

        Map<String, Object> vehicle = new HashMap<>();
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_YEAR, _year);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_MAKE, _make);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_MODEL, _model);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_DRIVE_TRAIN, _driveTrain);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_VIN_NUM, _vinNum);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_ODOMETER, odometer);
        vehicle.put(FirebaseUtil.VEHICLES_FIELD_IS_ACTIVE, _isActive);

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS + "/" + account.getDocID() + "/" + FirebaseUtil.COLLECTION_VEHICLES).document(vehicleID)
                .update(vehicle)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseFirestoreException){
                            Log.i(TAG, "onFailure: Error Code: " + ((FirebaseFirestoreException) e).getCode());
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        ToastUtil.vehicleUpdated(getContext());
                        Intent resultIntent = new Intent();
                        getActivity().setResult(Activity.RESULT_OK, resultIntent);
                        getActivity().finish();
                    }
                });

    }



}
