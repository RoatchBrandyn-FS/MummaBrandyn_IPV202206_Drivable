package com.example.drivable.data_objects;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class Account implements Serializable {

    //starting test variables
    private final String docID;
    private final String accountImageRef;
    private final String company;
    private final String companyAcronym;
    private final String firstName;
    private final String lastName;
    // *** MORE VARIABLES TO BE ADDED AFTER POC ***

    public Account (String _docID, String _accountImageRef, String _company, String _companyAcronym, String _firstName, String _lastName){
        docID = _docID;
        accountImageRef = _accountImageRef;
        company = _company;
        companyAcronym = _companyAcronym;
        firstName = _firstName;
        lastName = _lastName;
    }

    public String getDocID() {
        return docID;
    }

    public String getAccountImageRef() {
        return accountImageRef;
    }

    public String getCompany() {
        return company;
    }

    public String getCompanyAcronym() {
        return companyAcronym;
    }

    public String getName(){
        return firstName + " " + lastName;
    }

    private Uri getImageUri(String _accountImageRef){
        final Uri[] accountImageUri = {null};

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathRef = storageRef.child(_accountImageRef);

        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                accountImageUri[0] = uri;
            }
        });

        return accountImageUri[0];
    }
}
