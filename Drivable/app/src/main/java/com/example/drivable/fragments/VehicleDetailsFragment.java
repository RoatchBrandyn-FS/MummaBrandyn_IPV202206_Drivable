package com.example.drivable.fragments;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.data_objects.Vehicle;

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

        Vehicle selectedVehicle = vehicleDetailsFragmentListener.getVehicle();

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
        nameTV.setText(selectedVehicle.getName());
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
}
