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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.activities.EditProfileActivity;
import com.example.drivable.activities.SignInActivity;
import com.example.drivable.activities.UpdateEmailActivity;
import com.example.drivable.activities.UpdatePasswordActivity;
import com.example.drivable.data_objects.Account;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.example.drivable.utilities.ToastUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
        void updateAccount(Account updatedAccount);
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
            updateEmailIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, profileFragmentListener.getAccount());

            startActivity(updateEmailIntent);
        }
        else if (view.getId() == R.id.profile_btn_update_password){
            //Update Password

            Intent updatePasswordIntent = new Intent(getContext(), UpdatePasswordActivity.class);
            updatePasswordIntent.setAction(Intent.ACTION_RUN);
            updatePasswordIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, profileFragmentListener.getAccount());

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

        if(item.getTitle().equals("Edit Profile")){
            Log.i(TAG, "onOptionsItemSelected: Should edit profile EXCEPT email and password");

            Intent editProfileIntent = new Intent(getContext(), EditProfileActivity.class);
            editProfileIntent.setAction(Intent.ACTION_RUN);
            editProfileIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, profileFragmentListener.getAccount());

            editProfileActivityLauncher.launch(editProfileIntent);
        }
        else if (item.getTitle().equals("Sign Out")){
            Intent signInIntent = new Intent(getContext(), SignInActivity.class);
            signInIntent.setAction(Intent.ACTION_MAIN);
            signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            FirebaseUtil.mAuth.signOut();
            ToastUtil.accountSignedOut(getContext());
            startActivity(signInIntent);
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

    ActivityResultLauncher<Intent> editProfileActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    FirebaseUser user = FirebaseUtil.mAuth.getCurrentUser();
                    final Account[] account = new Account[1];

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).get().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("FirebaseUtils.TAG", "onFailure: ");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                String _userID = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_USERID);

                                if(_userID.equals(user.getUid())){
                                    String _accountImageRef = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_ACCOUNT_IMAGE_REF);
                                    String _company = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_COMPANY);
                                    String _companyAcronym = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_COMPANY_ACRONYM);
                                    String _firstName = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_FIRST_NAME);
                                    String _lastName = doc.getString(FirebaseUtil.ACCOUNTS_FIELD_LAST_NAME);

                                    account[0] = new Account(doc.getId(), _accountImageRef, _company, _companyAcronym, _firstName, _lastName);
                                }
                            }

                            profileFragmentListener.updateAccount(account[0]);

                            //get elements
                            Account userAccount = profileFragmentListener.getAccount();
                            TextView companyTV = getActivity().findViewById(R.id.profile_tv_company);
                            TextView acronymTV = getActivity().findViewById(R.id.profile_tv_acronym);
                            TextView ownerTV = getActivity().findViewById(R.id.profile_tv_owner);
                            TextView emailTV = getActivity().findViewById(R.id.profile_tv_email);

                            //set elements with data
                            loadImage(userAccount.getAccountImageRef());
                            companyTV.setText(userAccount.getCompany());
                            acronymTV.setText(userAccount.getCompanyAcronym());
                            ownerTV.setText(userAccount.getName());
                            emailTV.setText(FirebaseUtil.mAuth.getCurrentUser().getEmail());

                        }

                    });

                }
            });

}
