package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Shop;
import com.example.drivable.fragments.ShopDetailsFragment;
import com.example.drivable.fragments.ShopsListFragment;
import com.example.drivable.utilities.IntentExtrasUtil;

public class ShopDetailsActivity extends AppCompatActivity implements ShopDetailsFragment.ShopDetailsFragmentListener {

    Account userAccount;
    Shop selectedShop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Shop Details");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_baseline_home_32));
        }

        //set account data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);
        selectedShop = (Shop) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_SHOP);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.fragment_container, ShopDetailsFragment.newInstance()).commit();
    }

    @Override
    public Shop getShop() {
        return selectedShop;
    }

    @Override
    public Account getAccount() {
        return userAccount;
    }
}
