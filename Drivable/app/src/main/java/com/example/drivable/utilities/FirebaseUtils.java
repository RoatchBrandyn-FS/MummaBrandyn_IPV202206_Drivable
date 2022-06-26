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

    //Firebase Auth and Account
    public static FirebaseAuth mAuth = null;
    public static Account userAccount;


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
