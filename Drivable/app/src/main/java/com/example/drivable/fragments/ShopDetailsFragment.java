package com.example.drivable.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Shop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ShopDetailsFragment extends Fragment implements View.OnClickListener {

    ShopDetailsFragmentListener shopDetailsFragmentListener;

    public static ShopDetailsFragment newInstance() {

        Bundle args = new Bundle();

        ShopDetailsFragment fragment = new ShopDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof ShopDetailsFragmentListener){
            shopDetailsFragmentListener = (ShopDetailsFragmentListener) context;
        }
    }

    public interface ShopDetailsFragmentListener{
        Shop getShop();
        Account getAccount();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        setElements();

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.shop_details_fab){

            Shop shop = shopDetailsFragmentListener.getShop();
            String address1 = shop.getAddressLine1();

            String uri = "geo:" + shop.getLat() + "," + shop.getLng() + "?q=" + shop.getLat() + "," +  shop.getLng();
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapsIntent.setPackage("com.google.android.apps.maps");

            if (mapsIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(mapsIntent);
            }
        }

    }

    private void setElements(){

        Shop selectedShop = shopDetailsFragmentListener.getShop();
        TextView nameTV = getActivity().findViewById(R.id.shop_details_tv_name);
        TextView addressTV = getActivity().findViewById(R.id.shop_details_tv_address);
        TextView shopTypeTV = getActivity().findViewById(R.id.shop_details_tv_shop_type);
        TextView descriptionTV = getActivity().findViewById(R.id.shop_details_tv_description);

        nameTV.setText(selectedShop.getName());

        String address = selectedShop.getAddressLine1() + "\n" + selectedShop.getAddressLine2();
        addressTV.setText(address);

        String shopTypeString = setShopTypeString(selectedShop);
        shopTypeTV.setText(shopTypeString);

        descriptionTV.setText(selectedShop.getDescription());

        FloatingActionButton shopDetailsFAB = getActivity().findViewById(R.id.shop_details_fab);
        shopDetailsFAB.setOnClickListener(this);

    }

    private String setShopTypeString(Shop shop){

        StringBuilder shopTypeBuilder = new StringBuilder();

        if(shop.isMaintenance()){
            shopTypeBuilder.append("- Maintenance\n");
        }
        if(shop.isOilChange()){
            shopTypeBuilder.append("- Oil Change\n");
        }
        if(shop.isTiresWheels()){
            shopTypeBuilder.append("- Tires/Wheels\n");
        }
        if(shop.isGlass()){
            shopTypeBuilder.append("- Glass\n");
        }
        if(shop.isBody()){
            shopTypeBuilder.append("- Body");
        }

        return shopTypeBuilder.toString();
    }

    private String setLocation(String address1){

        String[] split = address1.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;

        while(index < split.length){

            if(index == 0){
                stringBuilder.append(split[index]);
            }
            else{
                stringBuilder.append("+").append(split[index]);
            }

            index++;
        }

        return null;
    }

}
