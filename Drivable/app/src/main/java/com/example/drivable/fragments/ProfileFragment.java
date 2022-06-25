package com.example.drivable.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.utilities.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment.TAG";
    private ProfileFragmentListener profileFragmentListener;

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface ProfileFragmentListener{
        Account getAccount();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof ProfileFragmentListener){
            profileFragmentListener = (ProfileFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get elements
        Account account = profileFragmentListener.getAccount();
        TextView companyTV = getActivity().findViewById(R.id.profile_tv_company);
        TextView acronymTV = getActivity().findViewById(R.id.profile_tv_acronym);
        TextView ownerTV = getActivity().findViewById(R.id.profile_tv_owner);
        TextView emailTV = getActivity().findViewById(R.id.profile_tv_email);

        //set elements with data
        loadImage(account.getAccountImageRef());
        companyTV.setText(account.getCompany());
        acronymTV.setText(account.getCompanyAcronym());
        ownerTV.setText(account.getName());
        emailTV.setText(FirebaseUtils.mAuth.getCurrentUser().getEmail());


    }

    private void loadImage(String imageRef){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.child(imageRef).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i(TAG, "onSuccess: Uri loaded");
                ImageView accountImage = getActivity().findViewById(R.id.profile_iv_account_image);
                //accountImage.setImageURI(uri);

                Picasso.get().load(uri).into(accountImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: Uri Didn't Load");
            }
        });

    }

}
