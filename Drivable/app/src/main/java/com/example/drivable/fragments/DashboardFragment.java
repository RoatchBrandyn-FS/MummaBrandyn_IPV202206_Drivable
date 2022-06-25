package com.example.drivable.fragments;

import android.content.Context;
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
import com.example.drivable.data_objects.Account;
import com.example.drivable.utilities.DateUtil;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.Date;

public class DashboardFragment extends Fragment {

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
        TextView totalNumTV = getActivity().findViewById(R.id.dashboard_tv_total_num);
        TextView activeNumTV = getActivity().findViewById(R.id.dashboard_tv_active_num);
        TextView inactiveNumTV = getActivity().findViewById(R.id.dashboard_tv_inactive_num);

        //get current date
        String currentDate = DateUtil.setDateTime(new Date());

        //set text view values
        companyTV.setText(account.getCompany());
        dateTV.setText(currentDate);


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
        }

        return super.onOptionsItemSelected(item);
    }
}
