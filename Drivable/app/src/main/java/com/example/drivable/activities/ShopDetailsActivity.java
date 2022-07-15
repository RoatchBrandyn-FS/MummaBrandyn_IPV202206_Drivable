package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

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
        }

        //set account data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);
        selectedShop = (Shop) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_SHOP);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.fragment_container, ShopDetailsFragment.newInstance()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Shop getShop() {
        return selectedShop;
    }

    @Override
    public Account getAccount() {
        return userAccount;
    }

    @Override
    public void updateShop(Shop updatedShop) {
        selectedShop = updatedShop;
    }
}
