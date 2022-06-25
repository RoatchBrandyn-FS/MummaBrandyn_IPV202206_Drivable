package com.example.drivable.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drivable.R;
import com.example.drivable.fragments.SignUpFragment;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true).replace(R.id.fragment_container, SignUpFragment.newInstance())
                .commit();

    }
}
