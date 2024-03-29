package com.example.drivable.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Shop;
import com.example.drivable.utilities.AlertsUtil;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.example.drivable.utilities.ToastUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddShopActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "AddShopActivity.TAG";

    //variables
    Account userAccount;
    Shop selectedShop;
    boolean isEditing;

    String name;
    String addressLine1;
    String addressLine2;
    LatLng latLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_shop);

        //set account data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);
        isEditing = currentIntent.getBooleanExtra(IntentExtrasUtil.EXTRA_IS_EDITING, false);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){

            if(!isEditing){
                actionBar.setTitle("Add Shop");
            }
            else{
                actionBar.setTitle("Edit Shop");
            }
            actionBar.setHomeButtonEnabled(true);
        }

        setAddressSearch();

        Button shopBtn = findViewById(R.id.add_shop_btn_shop);
        shopBtn.setOnClickListener(this);

        if(isEditing){
            shopBtn.setText(getResources().getText(R.string.btn_update));
            selectedShop = (Shop) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_SHOP);
            setEditElements();
        }

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.add_shop_btn_shop){
            //error handle before saving
            errorHandle();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.i(TAG, "onOptionsItemSelected: item ID: " + item.getItemId());
        Log.i(TAG, "onOptionsItemSelected: button id: " + android.R.id.home);

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //set address search
    private void setAddressSearch(){

        String apiKey = "AIzaSyCUaNYK8fSJdbePxiFsbBLRMmklR62pvRo";

        //initialize places
        Places.initialize(getApplicationContext(), apiKey);

        //create client
        PlacesClient placesClient = Places.createClient(this);

        //Initialize AutoCompleteFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        //specify place data type return
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {

                Log.i(TAG, "onPlaceSelected: Name: " + place.getName());
                name = place.getName();
                Log.i(TAG, "onPlaceSelected: Address: " + place.getAddress());

                Log.i(TAG, "onPlaceSelected: Address: " + place.getAddressComponents().asList().size());
                Log.i(TAG, "onPlaceSelected: Address: " + place.getAddressComponents().asList().get(0).getName());
                Log.i(TAG, "onPlaceSelected: Address: " + place.getAddressComponents().asList().get(1).getName());
                Log.i(TAG, "onPlaceSelected: Address: " + place.getAddressComponents().asList().get(2).getName());
                Log.i(TAG, "onPlaceSelected: Address: " + place.getAddressComponents().asList().get(3).getName());
                Log.i(TAG, "onPlaceSelected: Address: " + place.getAddressComponents().asList().get(4).getName());
                Log.i(TAG, "onPlaceSelected: Address: " + place.getAddressComponents().asList().get(5).getName());
                Log.i(TAG, "onPlaceSelected: Address: " + place.getAddressComponents().asList().get(6).getName());

                addressLine1 = place.getAddressComponents().asList().get(0).getName() + " " + place.getAddressComponents().asList().get(1).getName();
                addressLine2 = place.getAddressComponents().asList().get(2).getName() + ", " + place.getAddressComponents().asList().get(4).getShortName() +
                        ", " + place.getAddressComponents().asList().get(6).getName() + " " + place.getAddressComponents().asList().get(5).getShortName();

                Log.i(TAG, "onPlaceSelected: Test Address Format: " + addressLine1 + "\n" + addressLine2);

                Log.i(TAG, "onPlaceSelected: Lat/Lng: " + place.getLatLng().latitude + "/" + place.getLatLng().longitude);
                latLng = place.getLatLng();

                setPlaceElements();

            }
        });

    }

    private void setPlaceElements(){

        TextView nameTV = findViewById(R.id.add_shop_tv_name);
        TextView addressLine1TV = findViewById(R.id.add_shop_tv_address_line_1);
        TextView addressLine2TV = findViewById(R.id.add_shop_tv_address_line_2);

        nameTV.setText(name);
        addressLine1TV.setText(addressLine1);
        addressLine2TV.setText(addressLine2);

    }

    private void setEditElements(){

        //get elements
        TextView nameTV = findViewById(R.id.add_shop_tv_name);
        TextView addressLine1TV = findViewById(R.id.add_shop_tv_address_line_1);
        TextView addressLine2TV = findViewById(R.id.add_shop_tv_address_line_2);
        CheckBox maintenanceCB = findViewById(R.id.add_shop_cb_maintenance);
        CheckBox oilChangeCB = findViewById(R.id.add_shop_cb_oil_change);
        CheckBox tiresWheelsCB = findViewById(R.id.add_shop_cb_tires_wheels);
        CheckBox glassCB = findViewById(R.id.add_shop_cb_glass);
        CheckBox bodyCB = findViewById(R.id.add_shop_cb_body);
        EditText descriptionET = findViewById(R.id.add_shop_et_description);
        EditText nicknameET = findViewById(R.id.add_shop_et_nickname);

        //set elements
        nameTV.setText(selectedShop.getName());
        addressLine1TV.setText(selectedShop.getAddressLine1());
        addressLine2TV.setText(selectedShop.getAddressLine2());
        maintenanceCB.setChecked(selectedShop.isMaintenance());
        oilChangeCB.setChecked(selectedShop.isOilChange());
        tiresWheelsCB.setChecked(selectedShop.isTiresWheels());
        glassCB.setChecked(selectedShop.isGlass());
        bodyCB.setChecked(selectedShop.isBody());
        descriptionET.setText(selectedShop.getDescription());
        latLng = new LatLng(selectedShop.getLat(), selectedShop.getLng());
        nicknameET.setText(selectedShop.getNickname());

    }

    private void errorHandle(){

        //get elements
        TextView nameTV = findViewById(R.id.add_shop_tv_name);
        TextView addressLine1TV = findViewById(R.id.add_shop_tv_address_line_1);
        TextView addressLine2TV = findViewById(R.id.add_shop_tv_address_line_2);
        CheckBox maintenanceCB = findViewById(R.id.add_shop_cb_maintenance);
        CheckBox oilChangeCB = findViewById(R.id.add_shop_cb_oil_change);
        CheckBox tiresWheelsCB = findViewById(R.id.add_shop_cb_tires_wheels);
        CheckBox glassCB = findViewById(R.id.add_shop_cb_glass);
        CheckBox bodyCB = findViewById(R.id.add_shop_cb_body);
        EditText descriptionET = findViewById(R.id.add_shop_et_description);
        EditText nicknameET = findViewById(R.id.add_shop_et_nickname);

        //get strings and bools
        String nameString = nameTV.getText().toString().trim();
        String addressLine1String = addressLine1TV.getText().toString().trim();
        String addressLine2String = addressLine2TV.getText().toString().trim();
        boolean isMaintenance = maintenanceCB.isChecked();
        boolean isOilChange = oilChangeCB.isChecked();
        boolean isTiresWheels = tiresWheelsCB.isChecked();
        boolean isGlass = glassCB.isChecked();
        boolean isBody = bodyCB.isChecked();
        String descriptionString = descriptionET.getText().toString().trim();
        String nicknameString = nicknameET.getText().toString().trim();


        if(nameString.equals("Name") || addressLine1String.equals("Line 1") || addressLine2String.equals("Line 2") || latLng == null){
            AlertsUtil.addShopNoAddress(this);
        }
        else if(!isMaintenance && !isOilChange && !isTiresWheels && !isGlass && !isBody){
            AlertsUtil.addShopTypeMin(this);
        }
        else if(descriptionString.isEmpty() || nicknameString.isEmpty()){
            AlertsUtil.addShopDescriptionEmpty(this);
        }
        else {
            if(!isEditing){
                addShop(this, nameString, addressLine1String, addressLine2String, isMaintenance, isOilChange, isTiresWheels, isGlass,
                        isBody, descriptionString, nicknameString);
            }
            else{
                //editing
                editShop(this, nameString, addressLine1String, addressLine2String, isMaintenance, isOilChange, isTiresWheels, isGlass,
                        isBody, descriptionString, nicknameString);
            }

        }


    }

    private void addShop(Context context, String _name, String _addressLine1, String _addressLine2, boolean _isMaintenance, boolean _isOilChange, boolean _isTiresWheels,
                         boolean _isGlass, boolean _isBody, String _description, String _nickname){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> shop = new HashMap<>();
        shop.put(FirebaseUtil.SHOPS_FIELD_NAME, _name);
        shop.put(FirebaseUtil.SHOPS_FIELD_ADDRESS_1, _addressLine1);
        shop.put(FirebaseUtil.SHOPS_FIELD_ADDRESS_2, _addressLine2);
        shop.put(FirebaseUtil.SHOPS_FIELD_IS_MAINTENANCE, _isMaintenance);
        shop.put(FirebaseUtil.SHOPS_FIELD_IS_OIL_CHANGE, _isOilChange);
        shop.put(FirebaseUtil.SHOPS_FIELD_IS_TIRES_WHEELS, _isTiresWheels);
        shop.put(FirebaseUtil.SHOPS_FIELD_IS_GLASS, _isGlass);
        shop.put(FirebaseUtil.SHOPS_FIELD_IS_BODY, _isBody);
        shop.put(FirebaseUtil.SHOPS_FIELD_DESCRIPTION, _description);
        shop.put(FirebaseUtil.SHOPS_FIELD_NICKNAME, _nickname);

        double lat = latLng.latitude;
        double lng = latLng.longitude;
        GeoPoint geoPoint = new GeoPoint(lat, lng);

        shop.put(FirebaseUtil.SHOPS_FIELD_LATLNG, geoPoint);

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS + "/" + userAccount.getDocID() + "/" + FirebaseUtil.COLLECTION_SHOPS).add(shop)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: Error Code: " + ((FirebaseFirestoreException) e).getCode());
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        ToastUtil.shopAdded(context);
                        finish();
                    }
                });

    }

    private void editShop(Context context, String _name, String _addressLine1, String _addressLine2, boolean _isMaintenance, boolean _isOilChange, boolean _isTiresWheels,
                          boolean _isGlass, boolean _isBody, String _description, String _nickname){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> shop = new HashMap<>();
        shop.put(FirebaseUtil.SHOPS_FIELD_NAME, _name);
        shop.put(FirebaseUtil.SHOPS_FIELD_ADDRESS_1, _addressLine1);
        shop.put(FirebaseUtil.SHOPS_FIELD_ADDRESS_2, _addressLine2);
        shop.put(FirebaseUtil.SHOPS_FIELD_IS_MAINTENANCE, _isMaintenance);
        shop.put(FirebaseUtil.SHOPS_FIELD_IS_OIL_CHANGE, _isOilChange);
        shop.put(FirebaseUtil.SHOPS_FIELD_IS_TIRES_WHEELS, _isTiresWheels);
        shop.put(FirebaseUtil.SHOPS_FIELD_IS_GLASS, _isGlass);
        shop.put(FirebaseUtil.SHOPS_FIELD_IS_BODY, _isBody);
        shop.put(FirebaseUtil.SHOPS_FIELD_DESCRIPTION, _description);
        shop.put(FirebaseUtil.SHOPS_FIELD_NICKNAME, _nickname);

        double lat = latLng.latitude;
        double lng = latLng.longitude;
        GeoPoint geoPoint = new GeoPoint(lat, lng);

        shop.put(FirebaseUtil.SHOPS_FIELD_LATLNG, geoPoint);

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS + "/" + userAccount.getDocID() + "/" + FirebaseUtil.COLLECTION_SHOPS)
                .document(selectedShop.getDocID()).update(shop)
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
                        ToastUtil.shopUpdated(context);
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                });;
    }


}
