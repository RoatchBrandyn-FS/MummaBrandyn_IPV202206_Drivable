package com.example.drivable.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.drivable.R;
import com.example.drivable.data_objects.Account;
import com.example.drivable.fragments.DashboardFragment;
import com.example.drivable.fragments.EditProfileFragment;
import com.example.drivable.utilities.IntentExtrasUtil;

public class EditProfileActivity extends AppCompatActivity implements EditProfileFragment.EditProfileFragmentListener {

    private final String TAG = "EditProfActivity.TAG";
    Account userAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change Bar title to activity name
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Edit Profile");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_baseline_home_32));
        }

        //set room and user data
        Intent currentIntent = getIntent();
        userAccount = (Account) currentIntent.getSerializableExtra(IntentExtrasUtil.EXTRA_ACCOUNT);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragment_container, EditProfileFragment.newInstance()).commit();

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
}
