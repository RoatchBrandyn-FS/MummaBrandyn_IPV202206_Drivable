package com.example.drivable.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "UpdateEmailFrag.TAG";
    EditProfileFragmentListener editProfileFragmentListener;

    public static EditProfileFragment newInstance() {

        Bundle args = new Bundle();

        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface EditProfileFragmentListener{
        Account getAccount();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof EditProfileFragmentListener){
            editProfileFragmentListener = (EditProfileFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Account account = editProfileFragmentListener.getAccount();

        //get elements
        Button updateBtn = getActivity().findViewById(R.id.edit_profile_btn_update);
        EditText companyET = getActivity().findViewById(R.id.edit_profile_et_company);
        EditText acronymET = getActivity().findViewById(R.id.edit_profile_et_company_acronym);
        EditText firstNameET = getActivity().findViewById(R.id.edit_profile_et_first_name);
        EditText lastNameET = getActivity().findViewById(R.id.edit_profile_et_last_name);

        //set elements
        updateBtn.setOnClickListener(this);
        loadImage(account.getAccountImageRef());
        companyET.setText(account.getCompany());
        acronymET.setText(account.getCompanyAcronym());
        firstNameET.setText(account.getFirstName());
        lastNameET.setText(account.getLastName());

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.edit_profile_btn_update){
            //update here
        }
    }

    private void loadImage(String imageRef){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.child(imageRef).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i(TAG, "onSuccess: Uri loaded");
                ImageView accountImage = getActivity().findViewById(R.id.edit_profile_iv_main);
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
