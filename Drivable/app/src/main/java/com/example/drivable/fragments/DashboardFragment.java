package com.example.drivable.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.activities.FleetActivity;
import com.example.drivable.activities.ProfileActivity;
import com.example.drivable.activities.ShopsActivity;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.MaintenanceLog;
import com.example.drivable.data_objects.Shop;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.utilities.DateUtil;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.MapValue;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "DashboardFragment.TAG";
    private DashboardFragmentListener dashboardFragmentListener;

    public static DashboardFragment newInstance() {

        Bundle args = new Bundle();

        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface DashboardFragmentListener{
        Account getAccount();
        void updateAccount(Account updatedAccount);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof DashboardFragmentListener){
            dashboardFragmentListener = (DashboardFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set menu
        setHasOptionsMenu(true);

        //get account information
        Account account = dashboardFragmentListener.getAccount();

        //get text views to be changed
        TextView companyTV = getActivity().findViewById(R.id.dashboard_tv_company);
        TextView dateTV = getActivity().findViewById(R.id.dashboard_tv_date);

        //get current date
        String currentDate = DateUtil.setDateTime(new Date());

        //set text view values
        companyTV.setText(account.getCompany());
        dateTV.setText(currentDate);

        //set vehicle totals and shops
        setVehicleTotals(account);
        setShops(account);

        //set buttons
        Button myFleetBtn = getActivity().findViewById(R.id.dashboard_btn_my_fleet);
        myFleetBtn.setOnClickListener(this);
        Button myShopsBtn = getActivity().findViewById(R.id.dashboard_btn_my_shops);
        myShopsBtn.setOnClickListener(this);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Profile")){
            Log.i(TAG, "onOptionsItemSelected: Should open Profile");

            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
            profileIntent.setAction(Intent.ACTION_RUN);
            profileIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, dashboardFragmentListener.getAccount());

            profileActivityLauncher.launch(profileIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.dashboard_btn_my_fleet){

            Intent fleetIntent = new Intent(getContext(), FleetActivity.class);
            fleetIntent.setAction(Intent.ACTION_RUN);
            fleetIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, dashboardFragmentListener.getAccount());

            startActivity(fleetIntent);
        }
        else if(view.getId() == R.id.dashboard_btn_my_shops){

            Intent shopsIntent = new Intent(getContext(), ShopsActivity.class);
            shopsIntent.setAction(Intent.ACTION_RUN);
            shopsIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, dashboardFragmentListener.getAccount());

            startActivity(shopsIntent);

        }

    }

    private void setVehicleTotals(Account account){

        TextView totalNumTV = getActivity().findViewById(R.id.dashboard_tv_total_num);
        TextView activeNumTV = getActivity().findViewById(R.id.dashboard_tv_active_num);
        TextView inactiveNumTV = getActivity().findViewById(R.id.dashboard_tv_inactive_num);

        TextView atLotTV = getActivity().findViewById(R.id.dashboard_tv_lot_num);
        TextView notAtLotTV = getActivity().findViewById(R.id.dashboard_tv_shop_num);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).document(account.getDocID()).collection(FirebaseUtil.COLLECTION_VEHICLES).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.i(TAG, "onSuccess: Vehicles Loaded Successfully");
                //get values for vanTotals

                ArrayList<Vehicle> _vehicles = new ArrayList<>();
                ArrayList<Vehicle> activeVehicles = new ArrayList<>();
                ArrayList<Vehicle> inactiveVehicles = new ArrayList<>();
                ArrayList<Vehicle> atLotVehicles = new ArrayList<>();
                ArrayList<Vehicle> notAtLotVehicles = new ArrayList<>();

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

                    if(newVehicle.isActive()){
                        activeVehicles.add(newVehicle);
                    }
                    else if (!newVehicle.isActive() ) {

                        inactiveVehicles.add(newVehicle);

                        if(newVehicle.isAtLot() && !newVehicle.getDriveTrain().equals("NOT ASSIGNED") && !newVehicle.isAtLot()
                                && !newVehicle.getMake().equals("NOT ASSIGNED") && !newVehicle.getModel().equals("NOT ASSIGNED") && !newVehicle.getOdometer().equals("NA")
                                && !newVehicle.getVinNum().equals("NOT ASSIGNED") && !newVehicle.getYear().equals("NA")){
                            atLotVehicles.add(newVehicle);
                        }
                        else if (!newVehicle.isAtLot() && !newVehicle.getDriveTrain().equals("NOT ASSIGNED") && !newVehicle.isAtLot()
                                && !newVehicle.getMake().equals("NOT ASSIGNED") && !newVehicle.getModel().equals("NOT ASSIGNED") && !newVehicle.getOdometer().equals("NA")
                                && !newVehicle.getVinNum().equals("NOT ASSIGNED") && !newVehicle.getYear().equals("NA")){
                            notAtLotVehicles.add(newVehicle);
                        }

                    }

                    setMLogs(newVehicle, account);

                }

                account.updateVehicles(_vehicles);

                Log.i(TAG, "onSuccess: Vehicles List Size: " + account.getVehicles().size());
                totalNumTV.setText(Integer.toString(account.getVehicles().size()));
                activeNumTV.setText(Integer.toString(activeVehicles.size()));
                inactiveNumTV.setText(Integer.toString(inactiveVehicles.size()));

                atLotTV.setText(Integer.toString(atLotVehicles.size()));
                notAtLotTV.setText(Integer.toString(notAtLotVehicles.size()));

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

    private void setShops(Account account){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).document(account.getDocID()).collection(FirebaseUtil.COLLECTION_SHOPS).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: Error Code: " + ((FirebaseFirestoreException) e).getCode());
                    }
                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        ArrayList<Shop> updatedShops = new ArrayList<>();

                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){

                            String name = doc.getString(FirebaseUtil.SHOPS_FIELD_NAME);
                            String addressLine1 = doc.getString(FirebaseUtil.SHOPS_FIELD_ADDRESS_1);
                            String addressLine2 = doc.getString(FirebaseUtil.SHOPS_FIELD_ADDRESS_2);
                            boolean isMaintenance = doc.getBoolean(FirebaseUtil.SHOPS_FIELD_IS_MAINTENANCE);
                            boolean isOilChange = doc.getBoolean(FirebaseUtil.SHOPS_FIELD_IS_OIL_CHANGE);
                            boolean isTiresWheels = doc.getBoolean(FirebaseUtil.SHOPS_FIELD_IS_TIRES_WHEELS);
                            boolean isGlass = doc.getBoolean(FirebaseUtil.SHOPS_FIELD_IS_GLASS);
                            boolean isBody = doc.getBoolean(FirebaseUtil.SHOPS_FIELD_IS_BODY);
                            String description = doc.getString(FirebaseUtil.SHOPS_FIELD_DESCRIPTION);

                            GeoPoint geopoint = doc.getGeoPoint(FirebaseUtil.SHOPS_FIELD_LATLNG);
                            double lat = geopoint.getLatitude();
                            double lng = geopoint.getLongitude();
                            LatLng latLng = new LatLng(lat, lng);

                            Shop newShop = new Shop(doc.getId(), name, addressLine1, addressLine2, description, isMaintenance, isOilChange, isTiresWheels,
                                    isGlass, isBody, latLng);

                            updatedShops.add(newShop);

                        }

                        account.updateShops(updatedShops);
                    }
                });
    }

    private void setMLogs(Vehicle vehicle, Account account){

        ArrayList<MaintenanceLog> logs = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).document(account.getDocID()).collection(FirebaseUtil.COLLECTION_VEHICLES).document(vehicle.getDocID())
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

                            MaintenanceLog newLog = new MaintenanceLog(doc.getId(), name, date, logRefString, shopName, addressLine2, lat, lng, report);
                            logs.add(newLog);
                        }

                        vehicle.updateLogs(logs);

                    }
                });

    }

    ActivityResultLauncher<Intent> profileActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    FirebaseUser user = FirebaseUtil.mAuth.getCurrentUser();
                    final Account[] account = new Account[1];

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).get().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("FirebaseUtils.TAG", "onFailure: ");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                String _userID = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_USERID);

                                if(_userID.equals(user.getUid())){
                                    String _accountImageRef = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_ACCOUNT_IMAGE_REF);
                                    String _company = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_COMPANY);
                                    String _companyAcronym = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_COMPANY_ACRONYM);
                                    String _firstName = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_FIRST_NAME);
                                    String _lastName = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_LAST_NAME);

                                    account[0] = new Account(doc.getId(), _accountImageRef, _company, _companyAcronym, _firstName, _lastName);
                                }
                            }

                            dashboardFragmentListener.updateAccount(account[0]);

                            //get text views to be changed
                            TextView companyTV = getActivity().findViewById(R.id.dashboard_tv_company);
                            TextView dateTV = getActivity().findViewById(R.id.dashboard_tv_date);

                            //get current date
                            String currentDate = DateUtil.setDateTime(new Date());

                            //set text view values
                            companyTV.setText(dashboardFragmentListener.getAccount().getCompany());
                            dateTV.setText(currentDate);

                        }

                    });

                }
            });


}
