package com.example.drivable.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import com.example.drivable.utilities.AlertsUtil;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.ToastUtil;
import com.example.drivable.utilities.ValidationUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "SignInFragment.TAG";
    private final int REQUEST_CAMERA = 0x01001;

    public static SignUpFragment newInstance() {

        Bundle args = new Bundle();

        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set Buttons and Clickable
        Button signupBtn = getActivity().findViewById(R.id.signup_btn_signup);
        TextView chooseImageTV = getActivity().findViewById(R.id.signup_tv_choose_image);
        ImageView cameraIcon = getActivity().findViewById(R.id.signup_iv_camera);

        signupBtn.setOnClickListener(this);
        chooseImageTV.setOnClickListener(this);
        cameraIcon.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signup_btn_signup){
            //signupBtn
            Log.i(TAG, "onClick: Signup Button Pressed");

            validateSignup();
        }
        else if (view.getId() == R.id.signup_tv_choose_image){
            //chooseImageTV
            Log.i(TAG, "onClick: Choose Image Pressed");

            addImageAlert();
        }
        else if (view.getId() == R.id.signup_iv_camera){
            //cameraIcon
            Log.i(TAG, "onClick: Camera Icon Pressed");

            addImageAlert();
        }
    }

    private void validateSignup(){

        //set elements to check
        ImageView imageMain = getActivity().findViewById(R.id.signup_iv_main);
        EditText companyET = getActivity().findViewById(R.id.signup_et_company);
        EditText companyAcronymET = getActivity().findViewById(R.id.signup_et_company_acronym);
        EditText firstNameET = getActivity().findViewById(R.id.signup_et_first_name);
        EditText lastNameET = getActivity().findViewById(R.id.signup_et_last_name);
        EditText emailET = getActivity().findViewById(R.id.signup_et_email);
        EditText passwordET = getActivity().findViewById(R.id.signup_et_password);
        EditText confirmPasswordET = getActivity().findViewById(R.id.signup_et_confirm_password);

        //get strings for each element to check
        String imageMainTag = imageMain.getTag().toString();
        Log.i(TAG, "validateSignup: Image = " + imageMainTag);
        //image_placeholder tag = 0
        //Camera Photo = 1
        //CHOOSE IMAGE = 2
        String companyString = companyET.getText().toString().trim();
        String companyAcronymString = companyAcronymET.getText().toString().trim();
        String firstNameString = firstNameET.getText().toString().trim();
        String lastNameString = lastNameET.getText().toString().trim();
        String emailString = emailET.getText().toString().trim();
        String passwordString = passwordET.getText().toString().trim();
        String confirmPasswordString = confirmPasswordET.getText().toString().trim();

        if(imageMainTag.equals("0")){
            AlertsUtil.signupImageError(getContext());
            passwordET.setText("");
            confirmPasswordET.setText("");
        }
        else{

            if(companyString.isEmpty() || companyAcronymString.isEmpty() || firstNameString.isEmpty() || lastNameString.isEmpty() || emailString.isEmpty() ||
                    passwordString.isEmpty() || confirmPasswordString.isEmpty()){

                AlertsUtil.signupEmptyError(getContext());
                passwordET.setText("");
                confirmPasswordET.setText("");
            }
            else{

                if(!ValidationUtil.isEmail(emailString)){
                    AlertsUtil.emailError(getContext());
                    passwordET.setText("");
                    confirmPasswordET.setText("");
                }
                else{

                    if(!passwordString.equals(confirmPasswordString)){
                        AlertsUtil.passwordMatchError(getContext());
                    }
                    else{
                        if(passwordString.length() < 6){
                            AlertsUtil.passwordMinError(getContext());
                            passwordET.setText("");
                            confirmPasswordET.setText("");
                        }
                        else{
                            signupUser(companyString, companyAcronymString, firstNameString, lastNameString, emailString, passwordString, imageMain);
                        }

                    }

                }


            }

        }

    }

    private void signupUser(String company, String acronym, String firstName, String lastName, String email, String password, ImageView mainImage){

        progressbarOn();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressbarOff();
                clearPasswords();

                Log.i(TAG, "onFailure: " + e);
                if(e instanceof FirebaseAuthException){

                    String errorCode = ((FirebaseAuthException) e).getErrorCode();
                    Log.i(TAG, "onFailure: " + errorCode);

                    switch (errorCode){
                        case "ERROR_EMAIL_ALREADY_IN_USE":
                            AlertsUtil.emailDuplicateError(getContext());
                    }

                }
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(TAG, "onComplete: User Created Successful");
                    FirebaseUser user = mAuth.getCurrentUser();

                    Log.i(TAG, "onComplete: UID = " + user.getUid());
                    saveAccountImage(company, acronym, firstName, lastName, mainImage, user.getUid());
                }
                else {
                    Log.i(TAG, "onComplete: User Not Created");
                    Log.i(TAG, "onComplete: " + task.getException());
                    clearPasswords();

                }
            }
        });

    }

    private void saveAccountImage(String company, String acronym, String firstName, String lastName, ImageView mainImage, String userID){
        // get storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // create ref for image
        StorageReference accountImageRef = storageRef.child(FirebaseUtil.STORAGE_ACCOUNTS + userID + "/" + FirebaseUtil.STORAGE_IMAGES
                + FirebaseUtil.STORAGE_ACCOUNT_IMAGE);

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
                clearPasswords();

                Log.i(TAG, "onFailure: Image no uploaded correctly");
                Log.i(TAG, "onFailure: Error: " + e.getLocalizedMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "onSuccess: Image upload successful!");
                Log.i(TAG, "onSuccess: Image Path = " + taskSnapshot.getStorage().getPath());
                accountSetup(company, acronym, firstName, lastName, userID, taskSnapshot.getStorage().getPath());
            }
        });

    }

    private void accountSetup(String company, String acronym, String firstName, String lastName, String userID, String accountImageRef){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //set hash map
        Map<String, Object> newAccount = new HashMap<>();
        newAccount.put(FirebaseUtil.ACCOUNTS_FIELD_USERID, userID);
        newAccount.put(FirebaseUtil.ACCOUNTS_FIELD_ACCOUNT_IMAGE_REF, accountImageRef);
        newAccount.put(FirebaseUtil.ACCOUNTS_FIELD_COMPANY, company);
        newAccount.put(FirebaseUtil.ACCOUNTS_FIELD_COMPANY_ACRONYM, acronym);
        newAccount.put(FirebaseUtil.ACCOUNTS_FIELD_FIRST_NAME, firstName);
        newAccount.put(FirebaseUtil.ACCOUNTS_FIELD_LAST_NAME, lastName);

        db.collection(FirebaseUtil.COLLECTION_ACCOUNTS).add(newAccount).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                progressbarOff();
                ToastUtil.accountCreated(getContext());

                Log.i(TAG, "onSuccess: Doc added with id = " + documentReference.getId());
                getActivity().finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseFirestoreException){

                    progressbarOff();
                    clearPasswords();

                    Log.i(TAG, "onFailure: Error Code: " + ((FirebaseFirestoreException) e).getCode());
                }

            }
        });

    }

    private void progressbarOn(){
        RelativeLayout progressbarView = getActivity().findViewById(R.id.signup_progressbar_view);
        ProgressBar progressBar = getActivity().findViewById(R.id.signup_progressbar);

        progressbarView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        progressBar.setActivated(true);
    }

    private void progressbarOff(){
        RelativeLayout progressbarView = getActivity().findViewById(R.id.signup_progressbar_view);
        ProgressBar progressBar = getActivity().findViewById(R.id.signup_progressbar);

        progressBar.setActivated(false);
        progressbarView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void clearPasswords(){
        EditText passwordET = getActivity().findViewById(R.id.signup_et_password);
        EditText confirmPasswordET = getActivity().findViewById(R.id.signup_et_confirm_password);

        passwordET.setText("");
        confirmPasswordET.setText("");
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

    ActivityResultLauncher<Intent> takePhotoActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i(TAG, "onActivityResult: Result Code = " + result.getResultCode());

                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.i(TAG, "onActivityResult: Data found after selecting image");
                        Bitmap takenPhoto = (Bitmap) result.getData().getExtras().get("data");

                        ImageView mainImage = getActivity().findViewById(R.id.signup_iv_main);
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

                        ImageView mainImage = getActivity().findViewById(R.id.signup_iv_main);
                        mainImage.setImageURI(selectedImage);
                        mainImage.setTag(2);
                    }
                    else {
                        Log.i(TAG, "onActivityResult: Error getting image data");
                    }

                }
            }
    );

}
