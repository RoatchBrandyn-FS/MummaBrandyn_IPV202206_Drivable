package com.example.drivable.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.activities.AddShopActivity;
import com.example.drivable.activities.AddVehicleActivity;
import com.example.drivable.data_objects.Account;
import com.example.drivable.data_objects.Shop;
import com.example.drivable.data_objects.Vehicle;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShopDetailsFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "ShopDetailsFragment.TAG";
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
        void updateShop(Shop updatedShop);
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_shop_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals("Edit Shop")){
            //should open same add vehicle page but with values in the edit texts, spinner, and a different title
            Intent editShopIntent = new Intent(getContext(), AddShopActivity.class);
            editShopIntent.setAction(Intent.ACTION_RUN);
            editShopIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, shopDetailsFragmentListener.getAccount());
            editShopIntent.putExtra(IntentExtrasUtil.EXTRA_IS_EDITING, true);
            editShopIntent.putExtra(IntentExtrasUtil.EXTRA_SHOP, shopDetailsFragmentListener.getShop());

            editShopActivityLauncher.launch(editShopIntent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.shop_details_fab){

            Shop shop = shopDetailsFragmentListener.getShop();

            String uri = "geo:" + shop.getLat() + "," + shop.getLng() + "?q=" + shop.getLat() + "," +  shop.getLng();
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapsIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapsIntent);

            if (mapsIntent.resolveActivity(getContext().getPackageManager()) != null) {

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

    ActivityResultLauncher<Intent> editShopActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i(TAG, "onActivityResult: Result Code = " + result.getResultCode());


                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.i(TAG, "onActivityResult: Data for Vehicle Edited");

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Account account = shopDetailsFragmentListener.getAccount();
                        Shop selectedShop = shopDetailsFragmentListener.getShop();

                        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).document(account.getDocID()).collection(FirebaseUtil.COLLECTION_SHOPS)
                                .document(selectedShop.getDocID()).get().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot doc) {

                                        String name = doc.getString(FirebaseUtil.SHOPS_FIELD_NAME);
                                        String addressLine1 = doc.getString(FirebaseUtil.SHOPS_FIELD_ADDRESS_1);
                                        String addressLine2 = doc.getString(FirebaseUtil.SHOPS_FIELD_ADDRESS_2);
                                        boolean isMaintenance = doc.getBoolean(FirebaseUtil.SHOPS_FIELD_IS_MAINTENANCE);
                                        boolean isOilChange = doc.getBoolean(FirebaseUtil.SHOPS_FIELD_IS_OIL_CHANGE);
                                        boolean isTiresWheels = doc.getBoolean(FirebaseUtil.SHOPS_FIELD_IS_TIRES_WHEELS);
                                        boolean isGlass = doc.getBoolean(FirebaseUtil.SHOPS_FIELD_IS_GLASS);
                                        boolean isBody = doc.getBoolean(FirebaseUtil.SHOPS_FIELD_IS_BODY);
                                        String description = doc.getString(FirebaseUtil.SHOPS_FIELD_DESCRIPTION);

                                        GeoPoint geopoint = doc.getGeoPoint(FirebaseUtil.SHOPS_FIELD_LATLNG);
                                        double lat = geopoint.getLatitude();
                                        double lng = geopoint.getLongitude();
                                        LatLng latLng = new LatLng(lat, lng);

                                        Shop updatedShop = new Shop(doc.getId(), name, addressLine1, addressLine2, description, isMaintenance, isOilChange, isTiresWheels,
                                                isGlass, isBody, latLng);

                                        TextView nameTV = getActivity().findViewById(R.id.shop_details_tv_name);
                                        TextView addressTV = getActivity().findViewById(R.id.shop_details_tv_address);
                                        TextView shopTypeTV = getActivity().findViewById(R.id.shop_details_tv_shop_type);
                                        TextView descriptionTV = getActivity().findViewById(R.id.shop_details_tv_description);

                                        nameTV.setText(updatedShop.getName());

                                        String address = updatedShop.getAddressLine1() + "\n" + updatedShop.getAddressLine2();
                                        addressTV.setText(address);

                                        String shopTypeString = setShopTypeString(updatedShop);
                                        shopTypeTV.setText(shopTypeString);

                                        descriptionTV.setText(updatedShop.getDescription());

                                        shopDetailsFragmentListener.updateShop(updatedShop);

                                    }
                                });
                    }
                    else{
                        Log.i(TAG, "onActivityResult: Error resetting data or Back button pressed");
                    }


                }
            }
    );

}
