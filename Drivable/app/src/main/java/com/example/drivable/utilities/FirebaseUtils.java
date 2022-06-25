package com.example.drivable.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.drivable.data_objects.Account;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseUtils {

    //Firebase Auth
    public static FirebaseAuth mAuth = null;

    //Firebase Firestore Loaders
    public static Account loadAccount(String userID){

        final Account[] account = {null};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_ACCOUNTS).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("FirebaseUtils.TAG", "onFailure: ");
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    String _userID = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_USERID);

                    if(_userID.equals(userID)){
                        String _accountImageRef = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_ACCOUNT_IMAGE_REF);
                        String _company = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_COMPANY);
                        String _companyAcronym = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_COMPANY_ACRONYM);
                        String _firstName = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_FIRST_NAME);
                        String _lastName = doc.getString(FirebaseUtils.ACCOUNTS_FIELD_LAST_NAME);

                        account[0] = new Account(doc.getId(), _accountImageRef, _company, _companyAcronym, _firstName, _lastName);
                    }
                }
            }
        });

        return account[0];
    }

    //Firebase Collections
    public static final String COLLECTION_ACCOUNTS = "accounts";

    //Accounts collections field keys
    public static final String ACCOUNTS_FIELD_USERID = "user_id";
    public static final String ACCOUNTS_FIELD_ACCOUNT_IMAGE_REF = "account_image_ref";
    public static final String ACCOUNTS_FIELD_COMPANY = "company";
    public static final String ACCOUNTS_FIELD_COMPANY_ACRONYM = "company_acronym";
    public static final String ACCOUNTS_FIELD_FIRST_NAME = "first_name";
    public static final String ACCOUNTS_FIELD_LAST_NAME = "last_name";

    //FirebaseStorage refs
    public static final String STORAGE_ACCOUNTS = "accounts/";
    public static final String STORAGE_IMAGES = "images/";
    public static final String STORAGE_LOGS = "logs/";
    public static final String STORAGE_ACCOUNT_IMAGE = "account_image";

}
