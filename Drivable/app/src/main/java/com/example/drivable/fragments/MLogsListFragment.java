package com.example.drivable.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.drivable.R;
import com.example.drivable.adapters.MLogAdapter;
import com.example.drivable.adapters.ShopListAdapter;
import com.example.drivable.data_objects.MaintenanceLog;
import com.example.drivable.data_objects.Shop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MLogsListFragment extends ListFragment {

    MLogListFragmentListener mLogListFragmentListener;

    public static MLogsListFragment newInstance() {

        Bundle args = new Bundle();

        MLogsListFragment fragment = new MLogsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface MLogListFragmentListener{
        ArrayList<MaintenanceLog> getLogs();
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

    private void setLogsList(){

        ArrayList<MaintenanceLog> logs = mLogListFragmentListener.getLogs();

        Collections.sort(logs, new Comparator<MaintenanceLog>() {
            @Override
            public int compare(MaintenanceLog m1, MaintenanceLog m2) {
                return m1.getName().compareTo(m2.getName());
            }
        });

        Collections.sort(logs, Collections.reverseOrder());

        MLogAdapter logAdapter = new MLogAdapter(getContext(), logs);

        setListAdapter(logAdapter);

    }

}
