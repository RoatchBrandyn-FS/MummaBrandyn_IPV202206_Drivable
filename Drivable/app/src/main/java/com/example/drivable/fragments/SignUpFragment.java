package com.example.drivable.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.drivable.R;
import com.example.drivable.utilities.AlertsUtil;
import com.example.drivable.utilities.ToastUtil;
import com.example.drivable.utilities.ValidationUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

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

            Intent chooseImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            chooseImageActivityLauncher.launch(chooseImageIntent);
        }
        else if (view.getId() == R.id.signup_iv_camera){
            //cameraIcon
            Log.i(TAG, "onClick: Camera Icon Pressed");



            checkCameraPermissions();
        }
    }

    private void validateSignup(){

        //set elements to check
        ImageView imageMain = getActivity().findViewById(R.id.signup_iv_main);
        EditText compnayET = getActivity().findViewById(R.id.signup_et_company);
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
        String companyString = compnayET.getText().toString().trim();
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
                        //Everything validated and can save information
                        signupUser(companyString, companyAcronymString, firstNameString, lastNameString, emailString, passwordString, imageMain);
                    }

                }


            }

        }

    }

    private void signupUser(String company, String acronym, String firstName, String lastName, String email, String password, ImageView mainImage){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e);
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(TAG, "onComplete: User Created Successful");
                    FirebaseUser user = mAuth.getCurrentUser();

                    Log.i(TAG, "onComplete: UID = " + user.getUid());
                }
                else {
                    Log.i(TAG, "onComplete: User Not Created");
                    Log.i(TAG, "onComplete: " + task.getException().getLocalizedMessage());
                }
            }
        });

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
