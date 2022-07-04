package com.example.drivable.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drivable.R;
import com.example.drivable.data_objects.Vehicle;

import java.util.ArrayList;

public class VehicleAdapter extends BaseAdapter {

    //base id
    private static final long BASE_ID = 0x1011;

    //ref screen
    private final Context context;

    //ref collection
    private final ArrayList<Vehicle> vehicles;

    //ref company acronym
    private final String acronym;

    public VehicleAdapter(Context _context, ArrayList<Vehicle> _vehicles, String _acronym){
        context = _context;
        vehicles = _vehicles;
        acronym = _acronym;
    }


    @Override
    public int getCount() {
        if(vehicles != null){
            return vehicles.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(vehicles != null && position >= 0 && position < vehicles.size()){
            return vehicles.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return BASE_ID + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        String vehicleName = vehicles.get(position).getName();
        String vehicleVin = vehicles.get(position).getVinNum();
        boolean isActive = vehicles.get(position).isActive();
        String uri = "";
        if(vehicles.get(position).getMake().toLowerCase().equals("NOT ASSIGNED")){
            uri = "@drawable/image_placeholder";
        }
        else {
            uri = "@drawable/" + vehicles.get(position).getMake().toLowerCase();
        }

        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.base_vehicle_adapter, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        else{
            vh = (ViewHolder)convertView.getTag();
        }

        if (vehicleName != null && vehicleVin != null && vehicles.get(position).getMake() != null){
            // *** SET VIEWHOLDER HERE ***

            String name = acronym + "-" + vehicleName;

            vh.ba_image.setImageResource(imageResource);
            vh.ba_tv_main.setText(name);
            vh.ba_tv_sub.setText(vehicleVin);

            String active = "ACTIVE";
            String inactive = "INACTIVE";

            if(isActive){
                vh.ba_tv_status.setText(active);
                vh.ba_tv_status.setTextColor(context.getResources().getColor(R.color.green));
            }
            else{
                vh.ba_tv_status.setText(inactive);
                vh.ba_tv_status.setTextColor(context.getResources().getColor(R.color.red));
            }
        }

        return convertView;
    }

    //view holder
    static class ViewHolder{
        final ImageView ba_image;
        final TextView ba_tv_main;
        final TextView ba_tv_sub;
        final TextView ba_tv_status;

        public ViewHolder(View _layout){
            ba_image = _layout.findViewById(R.id.ba_make_image);
            ba_tv_main = _layout.findViewById(R.id.ba_vehicle_tv_main);
            ba_tv_sub = _layout.findViewById(R.id.ba_vehicle_tv_sub);
            ba_tv_status = _layout.findViewById(R.id.ba_vehicle_tv_status);
        }
    }

}
