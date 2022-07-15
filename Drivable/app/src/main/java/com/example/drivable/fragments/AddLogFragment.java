package com.example.drivable.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.adapters.ShopListAdapter;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Shop;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.utilities.AlertsUtil;
import com.example.drivable.utilities.DateUtil;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.ToastUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddLogFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "AddLogFragment.TAG";
    private final int REQUEST_CAMERA = 0x01001;

    private AddLogFragmentListener addLogFragmentListener;

    public static AddLogFragment newInstance() {

        Bundle args = new Bundle();

        AddLogFragment fragment = new AddLogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface AddLogFragmentListener{
        Account getAccount();
        Vehicle getVehicle();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof AddLogFragmentListener){
            addLogFragmentListener = (AddLogFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setButtons();
        setSpinner();
        setTitleView();

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.add_log_tv_choose_image){
            //choose image from gallery

            Intent chooseImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            chooseImageActivityLauncher.launch(chooseImageIntent);
        }
        else if(view.getId() == R.id.add_log_iv_camera){
            //take image from camera

            checkCameraPermissions();
        }
        else if(view.getId() == R.id.add_log_btn_add){
            validateData();
        }

    }

    private void setButtons(){

        TextView chooseImageTV = getActivity().findViewById(R.id.add_log_tv_choose_image);
        ImageView cameraIcon = getActivity().findViewById(R.id.add_log_iv_camera);
        Button addBtn = getActivity().findViewById(R.id.add_log_btn_add);

        chooseImageTV.setOnClickListener(this);
        cameraIcon.setOnClickListener(this);
        addBtn.setOnClickListener(this);

    }

    private void setSpinner(){

        Spinner logSpinner = getActivity().findViewById(R.id.add_log_spinner);
        ArrayList<Shop> shops = addLogFragmentListener.getAccount().getShops();
        LatLng latLng = new LatLng(0,0 );

        Log.i(TAG, "setSpinner: Shops Size Before = " + shops.size());

        Shop defaultShop = new Shop("", "Lot", "", "Main Fleet Location", "",false,
                false, false, false, false, latLng, "Lot");

        shops.add(0, defaultShop);
        Log.i(TAG, "setSpinner: Shops Size After = " + shops.size());

        ShopListAdapter shopListAdapter = new ShopListAdapter(getContext(), shops);
        logSpinner.setAdapter(shopListAdapter);


    }

    private void setTitleView(){
        //get current date
        String currentDate = DateUtil.setDateTime(new Date());
        TextView dateTV = getActivity().findViewById(R.id.add_log_tv_date);
        dateTV.setText(currentDate);

        //get vehicle name
        Account account = addLogFragmentListener.getAccount();
        Vehicle vehicle = addLogFragmentListener.getVehicle();
        String name = account.getCompanyAcronym() + "-" + vehicle.getName();
        TextView nameTV = getActivity().findViewById(R.id.add_log_tv_vehicle_name);
        nameTV.setText(name);
    }

    private void checkCameraPermissions(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            //Camera Permission Granted
            Log.i(TAG, "checkPermissions: Camera Permission Granted");

            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            takePhotoActivityLauncher.launch(takePhotoIntent);
        }
        else{
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    private void validateData(){

        //get elements
        Account account = addLogFragmentListener.getAccount();
        Vehicle vehicle = addLogFragmentListener.getVehicle();

        TextView dateTV = getActivity().findViewById(R.id.add_log_tv_date);
        ImageView mainIV = getActivity().findViewById(R.id.add_log_iv_main);
        SwitchCompat activeSwitch = getActivity().findViewById(R.id.add_log_switch);
        Spinner logSpinner = getActivity().findViewById(R.id.add_log_spinner);
        EditText reportET = getActivity().findViewById(R.id.add_log_et_report);

        //get strings
        String dateString = dateTV.getText().toString().trim();
        String imageMainTag = mainIV.getTag().toString();
        //image_placeholder tag = 0
        //Camera Photo = 1
        //CHOOSE IMAGE = 2
        boolean isActive = activeSwitch.isChecked();

        int index = logSpinner.getSelectedItemPosition();
        Shop selectedShop;

        if(index == 0){

            LatLng latLng = new LatLng(0,0 );

            selectedShop = new Shop("", "Lot", "", "Main Fleet Location", "",false,
                    false, false, false, false, latLng, "Lot");
        }
        else{
            selectedShop = addLogFragmentListener.getAccount().getShops().get(index);
        }

        Log.i(TAG, "validateData: selectedSHop name: " + selectedShop.getName());

        String reportString = reportET.getText().toString().trim();

        if(imageMainTag.equals("0")){
            AlertsUtil.addLogImageEmpty(getContext());
        }
        else if (!selectedShop.getName().equals("Lot") && isActive){
            AlertsUtil.addLogActiveStateIncorrect(getContext());
        }
        else if(reportString.isEmpty()){
            AlertsUtil.addLogReportEmpty(getContext());
        }
        else{
            saveLogImage(dateString, isActive, selectedShop, reportString, mainIV);
        }

    }

    private void saveLogImage(String date, boolean isActive, Shop selectedShop, String report, ImageView mainIV){

        progressbarOn();

        //get elements
        Account account = addLogFragmentListener.getAccount();
        Vehicle vehicle = addLogFragmentListener.getVehicle();
        FirebaseAuth mAuth = FirebaseUtil.mAuth;
        String fileName = "";

        if(vehicle.getLogs().size() < 9){
            fileName = "0" + Integer.toString(vehicle.getLogs().size() + 1);
        }
        else{
            fileName = Integer.toString(vehicle.getLogs().size() + 1);
        }



        // get storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // create ref for image
        StorageReference accountImageRef = storageRef.child(FirebaseUtil.STORAGE_ACCOUNTS + mAuth.getCurrentUser().getUid() + "/" + FirebaseUtil.STORAGE_IMAGES
                + FirebaseUtil.STORAGE_LOGS + vehicle.getDocID() + "_" + fileName);

        //set image bitmap
        mainIV.setDrawingCacheEnabled(true);
        mainIV.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) mainIV.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = accountImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressbarOff();

                Log.i(TAG, "onFailure: Image no uploaded correctly");
                Log.i(TAG, "onFailure: Error: " + e.getLocalizedMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "onSuccess: Image upload successful!");
                Log.i(TAG, "onSuccess: Image Path = " + taskSnapshot.getStorage().getPath());
                String fileName = "";

                if(vehicle.getLogs().size() < 9){
                    fileName = "0" + Integer.toString(vehicle.getLogs().size() + 1);
                }
                else{
                    fileName = Integer.toString(vehicle.getLogs().size() + 1);
                }

                saveToFirestore(taskSnapshot.getStorage().getPath(), fileName, date, isActive, selectedShop, report);
            }
        });

    }

    private void saveToFirestore(String logImageRef, String name, String date, boolean isActive, Shop selectedShop, String report){

        //get elements
        Account account = addLogFragmentListener.getAccount();
        Vehicle vehicle = addLogFragmentListener.getVehicle();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //set map for Maintenance Log
        Map<String, Object> newLog = new HashMap<>();
        newLog.put(FirebaseUtil.LOGS_FIELD_REF, logImageRef);
        newLog.put(FirebaseUtil.LOGS_FIELD_NAME, name);
        newLog.put(FirebaseUtil.LOGS_FIELD_DATE, date);
        newLog.put(FirebaseUtil.LOGS_FIELD_SHOP_NAME, selectedShop.getName());
        newLog.put(FirebaseUtil.LOGS_FIELD_ADDRESS_LINE_2, selectedShop.getAddressLine2());
        newLog.put(FirebaseUtil.LOGS_FIELD_LAT, selectedShop.getLat());
        newLog.put(FirebaseUtil.LOGS_FIELD_LNG, selectedShop.getLng());
        newLog.put(FirebaseUtil.LOGS_FIELD_REPORT, report);

        //Set map for Vehicle
        Map<String, Object> updateVehicle = new HashMap<>();
        updateVehicle.put(FirebaseUtil.VEHICLES_FIELD_IS_ACTIVE, isActive);

        if(!selectedShop.getName().equals("Lot")){
            updateVehicle.put(FirebaseUtil.VEHICLES_FIELD_IS_AT_LOT, false);
        }
        else{
            updateVehicle.put(FirebaseUtil.VEHICLES_FIELD_IS_AT_LOT, true);
        }

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS + "/" + account.getDocID() + "/" + FirebaseUtil.COLLECTION_VEHICLES + "/" + vehicle.getDocID()
                + "/" + FirebaseUtil.COLLECTION_LOGS).add(newLog).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseFirestoreException){
                    Log.i(TAG, "onFailure: Error Code: " + ((FirebaseFirestoreException) e).getCode());
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                db.collection(FirebaseUtil.COLLECTION_ACCOUNTS + "/" + account.getDocID() + "/" + FirebaseUtil.COLLECTION_VEHICLES)
                        .document(vehicle.getDocID())
                        .update(updateVehicle)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(e instanceof FirebaseFirestoreException){
                                    Log.i(TAG, "onFailure: Error Code: " + ((FirebaseFirestoreException) e).getCode());
                                    progressbarOff();
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                progressbarOff();

                                ToastUtil.logAdded(getContext());
                                Intent resultIntent = new Intent();
                                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                                getActivity().finish();
                            }
                        });

            }
        });


    }

    private void progressbarOn(){
        RelativeLayout progressbarView = getActivity().findViewById(R.id.add_log_progressbar_view);
        ProgressBar progressBar = getActivity().findViewById(R.id.add_log_progressbar);

        progressbarView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        progressBar.setActivated(true);
    }

    private void progressbarOff(){
        RelativeLayout progressbarView = getActivity().findViewById(R.id.add_log_progressbar_view);
        ProgressBar progressBar = getActivity().findViewById(R.id.add_log_progressbar);

        progressBar.setActivated(false);
        progressbarView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    ActivityResultLauncher<Intent> takePhotoActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i(TAG, "onActivityResult: Result Code = " + result.getResultCode());

                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.i(TAG, "onActivityResult: Data found after selecting image");
                        Bitmap takenPhoto = (Bitmap) result.getData().getExtras().get("data");

                        ImageView mainImage = getActivity().findViewById(R.id.add_log_iv_main);
                        mainImage.setImageBitmap(takenPhoto);
                        mainImage.setTag(1);
                    }
                    else {
                        Log.i(TAG, "onActivityResult: Error getting image data");
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> chooseImageActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i(TAG, "onActivityResult: Result Code = " + result.getResultCode());

                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Log.i(TAG, "onActivityResult: Data found after selecting image");
                        Uri selectedImage = result.getData().getData();

                        ImageView mainImage = getActivity().findViewById(R.id.add_log_iv_main);
                        mainImage.setImageURI(selectedImage);
                        mainImage.setTag(2);
                    }
                    else {
                        Log.i(TAG, "onActivityResult: Error getting image data");
                    }

                }
            }
    );


}
