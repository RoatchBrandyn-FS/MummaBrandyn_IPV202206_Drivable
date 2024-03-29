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

public class FirebaseUtil {

    //Firebase Auth
    public static FirebaseAuth mAuth = null;


    //Firebase Collections
    public static final String COLLECTION_ACCOUNTS = "accounts";
    public static final String COLLECTION_VEHICLES = "vehicles";
    public static final String COLLECTION_SHOPS = "shops";
    public static final String COLLECTION_LOGS = "logs";

    //Accounts collections field keys
    public static final String ACCOUNTS_FIELD_USERID = "user_id";
    public static final String ACCOUNTS_FIELD_ACCOUNT_IMAGE_REF = "account_image_ref";
    public static final String ACCOUNTS_FIELD_COMPANY = "company";
    public static final String ACCOUNTS_FIELD_COMPANY_ACRONYM = "company_acronym";
    public static final String ACCOUNTS_FIELD_FIRST_NAME = "first_name";
    public static final String ACCOUNTS_FIELD_LAST_NAME = "last_name";

    //Vehicles collections field keys
    public static final String VEHICLES_FIELD_NAME = "name";
    public static final String VEHICLES_FIELD_VIN_NUM = "vin_num";
    public static final String VEHICLES_FIELD_ODOMETER = "odometer";
    public static final String VEHICLES_FIELD_IS_ACTIVE = "isActive";
    public static final String VEHICLES_FIELD_YEAR = "year";
    public static final String VEHICLES_FIELD_MAKE = "make";
    public static final String VEHICLES_FIELD_MODEL = "model";
    public static final String VEHICLES_FIELD_DRIVE_TRAIN = "drive_train";
    public static final String VEHICLES_FIELD_IS_AT_LOT = "is_at_lot";

    //Shops collection field keys
    public static final String SHOPS_FIELD_NAME = "name";
    public static final String SHOPS_FIELD_ADDRESS_1 = "address_1";
    public static final String SHOPS_FIELD_ADDRESS_2 = "address_2";
    public static final String SHOPS_FIELD_IS_MAINTENANCE = "is_maintenance";
    public static final String SHOPS_FIELD_IS_OIL_CHANGE = "is_oil_change";
    public static final String SHOPS_FIELD_IS_TIRES_WHEELS = "is_tires_wheels";
    public static final String SHOPS_FIELD_IS_GLASS = "is_glass";
    public static final String SHOPS_FIELD_IS_BODY = "is_body";
    public static final String SHOPS_FIELD_DESCRIPTION = "description";
    public static final String SHOPS_FIELD_LATLNG = "lat_lng";
    public static final String SHOPS_FIELD_NICKNAME = "nickname";

    //Maintenance Logs collection fields
    public static final String LOGS_FIELD_NAME = "name";
    public static final String LOGS_FIELD_REF= "image_ref";
    public static final String LOGS_FIELD_DATE = "date";
    public static final String LOGS_FIELD_SHOP_NAME = "shop_name";
    public static final String LOGS_FIELD_ADDRESS_LINE_2 = "address_line_2";
    public static final String LOGS_FIELD_LAT= "lat";
    public static final String LOGS_FIELD_LNG= "lng";
    public static final String LOGS_FIELD_REPORT= "report";

    //FirebaseStorage refs
    public static final String STORAGE_ACCOUNTS = "accounts/";
    public static final String STORAGE_IMAGES = "images/";
    public static final String STORAGE_LOGS = "logs/";
    public static final String STORAGE_ACCOUNT_IMAGE = "account_image";

}
