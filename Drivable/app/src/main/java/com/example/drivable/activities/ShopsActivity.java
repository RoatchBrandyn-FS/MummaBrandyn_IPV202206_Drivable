package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Shop;
import com.example.drivable.fragments.FleetListFragment;
import com.example.drivable.fragments.ShopsListFragment;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ShopsActivity extends AppCompatActivity implements View.OnClickListener, ShopsListFragment.ShopsListFragmentListener {

    private final String TAG = "ShopsActivity.TAG";
    Account userAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Shops");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_baseline_home_32));
        }

        //set fab button
        FloatingActionButton fab = findViewById(R.id.activity_shops_fab);
        fab.setOnClickListener(this);

        //set account data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.activity_shops_container_list, ShopsListFragment.newInstance()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.i(TAG, "onOptionsItemSelected: item ID: " + item.getItemId());
        Log.i(TAG, "onOptionsItemSelected: button id: " + android.R.id.home);

        switch (item.getItemId()){
            case android.R.id.home:
                if(getParentActivityIntent() == null){
                    Log.i(TAG, "onOptionsItemSelected: Parent Activity not set in Manifest");
                    onBackPressed();
                }
                else{
                    Log.i(TAG, "onOptionsItemSelected: Should be sending to Dashboard");

                    Intent homeIntent = new Intent(this, DashboardActivity.class);
                    homeIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, userAccount);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    NavUtils.navigateUpTo(this, homeIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.activity_shops_fab){

            Log.i(TAG, "onClick: Send to Add Shop page");
            Intent addShopIntent = new Intent(this, AddShopActivity.class);
            addShopIntent.setAction(Intent.ACTION_RUN);
            addShopIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, userAccount);
            addShopIntent.putExtra(IntentExtrasUtil.EXTRA_IS_EDITING, false);

            startActivity(addShopIntent);
        }

    }

    @Override
    public ArrayList<Shop> getShopsList() {
        return userAccount.getShops();
    }

    @Override
    public Account getAccount() {
        return userAccount;
    }
}
