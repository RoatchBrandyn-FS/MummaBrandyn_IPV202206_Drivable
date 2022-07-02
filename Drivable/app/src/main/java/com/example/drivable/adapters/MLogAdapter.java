package com.example.drivable.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
