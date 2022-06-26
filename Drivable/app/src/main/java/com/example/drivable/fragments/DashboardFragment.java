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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.activities.ProfileActivity;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.utilities.DateUtil;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

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

        //set vehicle totals
        setVehicleTotals(account);

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

            startActivity(profileIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.dashboard_btn_my_fleet){

            Intent fleetIntent = new Intent();

        }

    }

    private void setVehicleTotals(Account account){

        TextView totalNumTV = getActivity().findViewById(R.id.dashboard_tv_total_num);
        TextView activeNumTV = getActivity().findViewById(R.id.dashboard_tv_active_num);
        TextView inactiveNumTV = getActivity().findViewById(R.id.dashboard_tv_inactive_num);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).document(account.getDocID()).collection(FirebaseUtil.COLLECTION_VEHICLES).get()
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
                    String _make = doc.getString(FirebaseUtil.VEHICLES_FIELD_MAKE);

                    Vehicle newVehicle = new Vehicle(doc.getId(), _name, _vinNum, _make);
                    _vehicles.add(newVehicle);
                }

                account.updateVehicles(_vehicles);

                Log.i(TAG, "onSuccess: Vehicles List Size: " + account.getVehicles().size());
                totalNumTV.setText(Integer.toString(account.getVehicles().size()));
                activeNumTV.setText("0");
                inactiveNumTV.setText("0");

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
}
