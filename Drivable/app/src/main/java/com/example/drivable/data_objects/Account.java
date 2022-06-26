package com.example.drivable.data_objects;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;

public class Account implements Serializable {

    //starting test variables
    private final String docID;
    private final String accountImageRef;
    private final String company;
    private final String companyAcronym;
    private final String firstName;
    private final String lastName;
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ArrayList<Vehicle> getVehicles(){
        return vehicles;
    }

    public void updateVehicles(ArrayList<Vehicle> updatedVehicles){
        vehicles = updatedVehicles;
    }
}
