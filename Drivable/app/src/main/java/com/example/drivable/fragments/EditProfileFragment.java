package com.example.drivable.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.activities.SignInActivity;
import com.example.drivable.data_objects.Account;
import com.example.drivable.utilities.AlertsUtil;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.ToastUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "UpdateEmailFrag.TAG";
    EditProfileFragmentListener editProfileFragmentListener;
    private final int REQUEST_CAMERA = 0x01001;

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
        ImageView cameraIcon = getActivity().findViewById(R.id.edit_profile_iv_camera);
        TextView chooseImage = getActivity().findViewById(R.id.edit_profile_tv_choose_image);

        //set elements
        updateBtn.setOnClickListener(this);
        cameraIcon.setOnClickListener(this);
        chooseImage.setOnClickListener(this);
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
            validateEdit();
        }
        else if (view.getId() == R.id.edit_profile_tv_choose_image){
            //chooseImageTV
            Log.i(TAG, "onClick: Choose Image Pressed");

            addImageAlert();
        }
        else if (view.getId() == R.id.edit_profile_iv_camera){
            //cameraIcon
            Log.i(TAG, "onClick: Camera Icon Pressed");

            addImageAlert();
        }
    }

    private void checkCameraPermissions(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            //Camera Permission Granted
            Log.i(TAG, "checkPermissions: Camera Permission Granted");

            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            takePhotoActivityLauncher.launch(takePhotoIntent);
        }
        else{
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    private void validateEdit(){

        //get elements
        Account account = editProfileFragmentListener.getAccount();
        ImageView accountImage = getActivity().findViewById(R.id.edit_profile_iv_main);
        EditText companyET = getActivity().findViewById(R.id.edit_profile_et_company);
        EditText acronymET = getActivity().findViewById(R.id.edit_profile_et_company_acronym);
        EditText firstNameET = getActivity().findViewById(R.id.edit_profile_et_first_name);
        EditText lastNameET = getActivity().findViewById(R.id.edit_profile_et_last_name);

        //get strings from edit text
        String companyString = companyET.getText().toString().trim();
        String acronymString = acronymET.getText().toString().trim();
        String firstNameString = firstNameET.getText().toString().trim();
        String lastNameString = lastNameET.getText().toString().trim();

        if(companyString.isEmpty() || acronymString.isEmpty() || firstNameString.isEmpty() || lastNameString.isEmpty()){

            AlertsUtil.editProfileEmptyError(getContext());
        }
        else {
            //edit with image first
            editProfileImage(companyString, acronymString, firstNameString, lastNameString, accountImage);
        }

    }

    private void editProfileImage(String company, String acronym, String firstName, String lastName, ImageView mainImage){

        progressbarOn();

        //get account for imageRef
        Account account = editProfileFragmentListener.getAccount();

        // get storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // create ref for image
        StorageReference accountImageRef = storageRef.child(account.getAccountImageRef());

        //set image bitmap
        mainImage.setDrawingCacheEnabled(true);
        mainImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) mainImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = accountImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressbarOff();

                if(e instanceof StorageException){
                    Log.i(TAG, "onFailure: Error Code: " + ((StorageException) e).getErrorCode());
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "onSuccess: Image upload successful!");
                Log.i(TAG, "onSuccess: Image Path = " + taskSnapshot.getStorage().getPath());
                editAccount(company, acronym, firstName, lastName);
            }
        });

    }

    private void editAccount(String company, String acronym, String firstName, String lastName){

        Account account = editProfileFragmentListener.getAccount();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //set hash map
        Map<String, Object> editAccount = new HashMap<>();
        editAccount.put(FirebaseUtil.ACCOUNTS_FIELD_COMPANY, company);
        editAccount.put(FirebaseUtil.ACCOUNTS_FIELD_COMPANY_ACRONYM, acronym);
        editAccount.put(FirebaseUtil.ACCOUNTS_FIELD_FIRST_NAME, firstName);
        editAccount.put(FirebaseUtil.ACCOUNTS_FIELD_LAST_NAME, lastName);

        db .collection(FirebaseUtil.COLLECTION_ACCOUNTS).document(account.getDocID()).update(editAccount).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseFirestoreException){

                    progressbarOff();

                    Log.i(TAG, "onFailure: Error Code: " + ((FirebaseFirestoreException) e).getCode());
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressbarOff();

                ToastUtil.accountUpdated(getContext());
                getActivity().finish();
            }
        });

    }

    private void progressbarOn(){
        RelativeLayout progressbarView = getActivity().findViewById(R.id.edit_profile_progressbar_view);
        ProgressBar progressBar = getActivity().findViewById(R.id.edit_profile_progressbar);

        progressbarView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        progressBar.setActivated(true);
    }

    private void progressbarOff(){
        RelativeLayout progressbarView = getActivity().findViewById(R.id.edit_profile_progressbar_view);
        ProgressBar progressBar = getActivity().findViewById(R.id.edit_profile_progressbar);

        progressBar.setActivated(false);
        progressbarView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
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

    private void addImageAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo");
        builder.setMessage("How do you want to add photo?");
        builder.setCancelable(true);
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //chooseImageTV
                Log.i(TAG, "onClick: Choose Image Pressed");

                Intent chooseImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                chooseImageActivityLauncher.launch(chooseImageIntent);

            }
        });
        builder.setNeutralButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                checkCameraPermissions();

            }
        });

        builder.show();

    }

    ActivityResultLauncher<Intent> takePhotoActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i(TAG, "onActivityResult: Result Code = " + result.getResultCode());

                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.i(TAG, "onActivityResult: Data found after selecting image");
                        Bitmap takenPhoto = (Bitmap) result.getData().getExtras().get("data");

                        ImageView mainImage = getActivity().findViewById(R.id.edit_profile_iv_main);
                        mainImage.setImageBitmap(takenPhoto);
                        mainImage.setTag(1);
                    }
                    else {
                        Log.i(TAG, "onActivityResult: Error getting image data");
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> chooseImageActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i(TAG, "onActivityResult: Result Code = " + result.getResultCode());

                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Log.i(TAG, "onActivityResult: Data found after selecting image");
                        Uri selectedImage = result.getData().getData();

                        ImageView mainImage = getActivity().findViewById(R.id.edit_profile_iv_main);
                        mainImage.setImageURI(selectedImage);
                        mainImage.setTag(1);
                    }
                    else {
                        Log.i(TAG, "onActivityResult: Error getting image data");
                    }

                }
            }
    );

}
