package com.example.drivable.acivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.fragments.SignInFragment;
import com.example.drivable.utilities.FirebaseUtils;
import com.example.drivable.utilities.NetworkUtil;
import com.example.drivable.utilities.ToastUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity implements SignInFragment.SignInFragmentListener {

    private final String TAG = "SignInActivity.TAG";
    ArrayList<Account> accounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(NetworkUtil.isConnected(this)){
            fetchFirebaseAccounts();
        }
        else{
            ToastUtil.networkError(this);
        }


        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true).replace(R.id.fragment_container, SignInFragment.newInstance())
                .commit();
    }

    private void fetchFirebaseAccounts(){

        Log.i(TAG, "fetchRooms: Should be loading account data");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FirebaseUtils.COLLECTION_ACCOUNTS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.i(TAG, "onComplete: Accounts Count = " + task.getResult().size());

                            for(QueryDocumentSnapshot accountDoc : task.getResult()){
                                String email = accountDoc.getString(FirebaseUtils.ACCOUNTS_FIELD_EMAIL);
                                String password = accountDoc.getString(FirebaseUtils.ACCOUNTS_FIELD_PASSWORD);
                                String docID = accountDoc.getId();
                                Log.i(TAG, "onComplete: Email = " + email);

                                Account newAccount = new Account(docID, email, password);
                                accounts.add(newAccount);
                            }

                        }
                        else if (task.isCanceled()){
                            Log.i(TAG, "onComplete: Accounts didn't load from Firebase correctly");
                        }
                    }
                });
    }

    @Override
    public ArrayList<Account> getAccounts() {
        return accounts;
    }
}