package com.example.drivable.acivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.drivable.R;
import com.example.drivable.fragments.SignInFragment;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true).replace(R.id.fragment_container, SignInFragment.newInstance())
                .commit();
    }
}