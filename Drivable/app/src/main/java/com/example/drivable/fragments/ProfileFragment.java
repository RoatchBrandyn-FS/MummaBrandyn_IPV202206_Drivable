package com.example.drivable.fragments;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.activities.UpdateEmailActivity;
import com.example.drivable.activities.UpdatePasswordActivity;
import com.example.drivable.data_objects.Account;
import com.example.drivable.utilities.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment implements View.OnClickListener {

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

        setHasOptionsMenu(true);

        //get elements
        Account account = profileFragmentListener.getAccount();
        TextView companyTV = getActivity().findViewById(R.id.profile_tv_company);
        TextView acronymTV = getActivity().findViewById(R.id.profile_tv_acronym);
        TextView ownerTV = getActivity().findViewById(R.id.profile_tv_owner);
        TextView emailTV = getActivity().findViewById(R.id.profile_tv_email);
        Button updateEmailBtn = getActivity().findViewById(R.id.profile_btn_update_email);
        Button updatePasswordBtn = getActivity().findViewById(R.id.profile_btn_update_password);

        //set elements with data
        loadImage(account.getAccountImageRef());
        companyTV.setText(account.getCompany());
        acronymTV.setText(account.getCompanyAcronym());
        ownerTV.setText(account.getName());
        emailTV.setText(FirebaseUtil.mAuth.getCurrentUser().getEmail());
        updateEmailBtn.setOnClickListener(this);
        updatePasswordBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.profile_btn_update_email){
            //Update Email

            Intent updateEmailIntent = new Intent(getContext(), UpdateEmailActivity.class);
            updateEmailIntent.setAction(Intent.ACTION_RUN);

            startActivity(updateEmailIntent);
        }
        else if (view.getId() == R.id.profile_btn_update_password){
            //Update Password

            Intent updatePasswordIntent = new Intent(getContext(), UpdatePasswordActivity.class);
            updatePasswordIntent.setAction(Intent.ACTION_RUN);

            startActivity(updatePasswordIntent);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getTitle() == "Edit Profile"){
            Log.i(TAG, "onOptionsItemSelected: Should edit profile EXCEPT email and password");
        }

        return super.onOptionsItemSelected(item);
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
