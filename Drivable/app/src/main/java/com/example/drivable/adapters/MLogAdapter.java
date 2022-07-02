package com.example.drivable.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.drivable.R;
import com.example.drivable.data_objects.MaintenanceLog;
import com.example.drivable.data_objects.Shop;

import java.util.ArrayList;

public class MLogAdapter extends BaseAdapter {

    private final String TAG = "MLogAdapter.TAG";

    //base id
    private static final long BASE_ID = 0x1011;

    //ref screen
    private final Context context;

    //ref collection
    private final ArrayList<MaintenanceLog> logs;

    public MLogAdapter(Context _context, ArrayList<MaintenanceLog> _logs){
        context = _context;
        logs = _logs;
    }

    @Override
    public int getCount() {
        if(logs != null){
            return logs.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(logs != null && position >= 0 && position < logs.size()){
            return logs.get(position);
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
        String logName = "Log #" + logs.get(position).getName();
        String date = logs.get(position).getDate();

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.base_log_adapter, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        }
        else{
            vh = (ViewHolder) convertView.getTag();
        }

        if (logName != null && date != null){
            // *** SET VIEWHOLDER HERE ***

            Log.i(TAG, "getView: " + logName);
            vh.ba_tv_main.setText(logName);
            vh.ba_tv_date.setText(date);
        }

        return convertView;
    }

    //view holder
    static class ViewHolder{
        final TextView ba_tv_main;
        final TextView ba_tv_date;

        public ViewHolder(View _layout){
            ba_tv_main = _layout.findViewById(R.id.ba_log_tv_main);
            ba_tv_date = _layout.findViewById(R.id.ba_log_tv_date);
        }
    }
}
