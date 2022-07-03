package com.example.drivable.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.drivable.R;
import com.example.drivable.activities.LogDetailsActivity;
import com.example.drivable.adapters.MLogAdapter;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.MaintenanceLog;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.utilities.IntentExtrasUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MLogsListFragment extends ListFragment {

    private final String TAG = "MLogListFragment.TAG";
    MLogListFragmentListener mLogListFragmentListener;

    public static MLogsListFragment newInstance() {

        Bundle args = new Bundle();

        MLogsListFragment fragment = new MLogsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface MLogListFragmentListener{
        ArrayList<MaintenanceLog> getLogs();
        Vehicle getLogVehicle();
        Account getLogAccount();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof MLogListFragmentListener){
            mLogListFragmentListener = (MLogListFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLogsList();

    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ArrayList<MaintenanceLog> logs = mLogListFragmentListener.getLogs();
        Log.i(TAG, "onListItemClick: ");

        Collections.sort(logs);

        Collections.sort(logs, Collections.reverseOrder());

        Intent logDetailsIntent = new Intent(getContext(), LogDetailsActivity.class);
        logDetailsIntent.setAction(Intent.ACTION_RUN);
        logDetailsIntent.putExtra(IntentExtrasUtil.EXTRA_LOG, logs.get(position));
        logDetailsIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, mLogListFragmentListener.getLogAccount());
        logDetailsIntent.putExtra(IntentExtrasUtil.EXTRA_VEHICLE, mLogListFragmentListener.getLogVehicle());

        startActivity(logDetailsIntent);

    }

    private void setLogsList(){

        ArrayList<MaintenanceLog> logs = mLogListFragmentListener.getLogs();
        Log.i(TAG, "setLogsList: logs size: " + logs.size());

        /*Collections.sort(logs, new Comparator<MaintenanceLog>() {
            @Override
            public int compare(MaintenanceLog m1, MaintenanceLog m2) {
                return m1.getName().compareTo(m2.getName());
            }
        });*/

        Collections.sort(logs, Collections.reverseOrder());

        MLogAdapter logAdapter = new MLogAdapter(getContext(), logs);

        setListAdapter(logAdapter);

    }

}
