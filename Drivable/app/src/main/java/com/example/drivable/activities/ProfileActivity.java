package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.fragments.DashboardFragment;
import com.example.drivable.fragments.ProfileFragment;
import com.example.drivable.utilities.FirebaseUtil;
import com.example.drivable.utilities.IntentExtrasUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.ProfileFragmentListener {

    private final String TAG = "ProfileActivity.TAG";
    Account userAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Profile");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_baseline_home_32));
        }

        //set room and user data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container, ProfileFragment.newInstance()).commit();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case android.R.id.home:
                if(getParentActivityIntent() == null){
                    onBackPressed();
                }
                else{

                    Intent homeIntent = new Intent(this, DashboardActivity.class);
                    homeIntent.putExtra(IntentExtrasUtil.EXTRA_ACCOUNT, userAccount);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    NavUtils.navigateUpTo(this, homeIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Account getAccount() {
        return userAccount;
    }

    @Override
    public void updateAccount(Account updatedAccount) {
        userAccount = updatedAccount;
    }

    private void updateAccount(){

        FirebaseUser user = FirebaseUtil.mAuth.getCurrentUser();

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

                        userAccount = new Account(doc.getId(), _accountImageRef, _company, _companyAcronym, _firstName, _lastName);
                    }
                }


            }

        });

    }
}
