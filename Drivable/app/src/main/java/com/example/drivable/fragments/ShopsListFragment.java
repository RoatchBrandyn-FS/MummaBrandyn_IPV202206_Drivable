package com.example.drivable.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.drivable.R;
import com.example.drivable.activities.ShopDetailsActivity;
import com.example.drivable.activities.VehicleDetailsActivity;
import com.example.drivable.adapters.ShopListAdapter;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Shop;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.utilities.IntentExtrasUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShopsListFragment extends ListFragment {

    ShopsListFragmentListener shopsListFragmentListener;

    public static ShopsListFragment newInstance() {

        Bundle args = new Bundle();

        ShopsListFragment fragment = new ShopsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface ShopsListFragmentListener{
        ArrayList<Shop> getShopsList();
        Account getAccount();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof ShopsListFragmentListener){
            shopsListFragmentListener = (ShopsListFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_shops, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setShopsList();
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ArrayList<Shop> shops = shopsListFragmentListener.getShopsList();
        Collections.sort(shops, new Comparator<Shop>() {
            @Override
            public int compare(Shop s1, Shop s2) {
                return s1.getName().compareTo(s2.getName());
            }
        });

        Intent shopDetailsIntent = new Intent(getContext(), ShopDetailsActivity.class);
        shopDetailsIntent.setAction(Intent.ACTION_RUN);
        shopDetailsIntent.putExtra(IntentExtrasUtil.EXTRA_SHOP, shops.get(position));
        shopDetailsIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, shopsListFragmentListener.getAccount());

        startActivity(shopDetailsIntent);

    }

    private void setShopsList(){


        ArrayList<Shop> shops = shopsListFragmentListener.getShopsList();
        Collections.sort(shops, new Comparator<Shop>() {
            @Override
            public int compare(Shop s1, Shop s2) {
                return s1.getName().compareTo(s2.getName());
            }
        });

        ShopListAdapter shopListAdapter = new ShopListAdapter(getContext(), shops);

        setListAdapter(shopListAdapter);

    }

}
