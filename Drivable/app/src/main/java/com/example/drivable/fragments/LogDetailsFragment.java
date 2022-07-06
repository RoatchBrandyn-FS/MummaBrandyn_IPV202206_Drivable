package com.example.drivable.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.MaintenanceLog;
import com.example.drivable.data_objects.Shop;
import com.example.drivable.data_objects.Vehicle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class LogDetailsFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "LogDetailsFragment.TAG";
    LogDetailsFragmentListener logDetailsFragmentListener;

    public static LogDetailsFragment newInstance() {

        Bundle args = new Bundle();

        LogDetailsFragment fragment = new LogDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface LogDetailsFragmentListener{
        MaintenanceLog getLog();
        Vehicle getVehicle();
        Account getAccount();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof LogDetailsFragmentListener){
            logDetailsFragmentListener = (LogDetailsFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_details, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setElements();
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.log_details_fab){

            MaintenanceLog log = logDetailsFragmentListener.getLog();

            String uri = "geo:" + log.getLat() + "," + log.getLng() + "?q=" + log.getLat() + "," +  log.getLng();
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapsIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapsIntent);

        }

    }

    private void setElements(){

        //get elements
        MaintenanceLog log = logDetailsFragmentListener.getLog();
        Vehicle vehicle = logDetailsFragmentListener.getVehicle();
        Account account = logDetailsFragmentListener.getAccount();
        TextView nameTV = getActivity().findViewById(R.id.log_details_tv_name);
        TextView logNumTV = getActivity().findViewById(R.id.log_details_tv_log_num);
        TextView dateTV = getActivity().findViewById(R.id.log_details_tv_date);
        TextView shopNameTV = getActivity().findViewById(R.id.log_details_tv_shop_name);
        TextView addressLine2TV = getActivity().findViewById(R.id.log_details_tv_address_line_2);
        TextView reportTV = getActivity().findViewById(R.id.log_details_tv_report);
        setFAB(log);


        //set elements
        String name = account.getCompanyAcronym() + "-" + vehicle.getName();
        nameTV.setText(name);
        String logNumString = "Log #" + log.getName();
        logNumTV.setText(logNumString);
        dateTV.setText(log.getDate());
        shopNameTV.setText(log.getShopName());
        addressLine2TV.setText(log.getAddressLine2());
        reportTV.setText(log.getReport());

        loadImage(log.getLogRefString());


    }

    private void loadImage(String imageRef){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.child(imageRef).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i(TAG, "onSuccess: Uri loaded");
                ImageView logImage = getActivity().findViewById(R.id.log_details_iv_image);
                //accountImage.setImageURI(uri);

                Picasso.get().load(uri).into(logImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: Uri Didn't Load");
            }
        });

    }

    private void setFAB(MaintenanceLog log){

        FloatingActionButton fab = getActivity().findViewById(R.id.log_details_fab);

        if(log.getShopName().equals("Lot")){
            fab.setVisibility(View.GONE);
        }
        else{
            fab.setOnClickListener(this);
        }

    }

}
