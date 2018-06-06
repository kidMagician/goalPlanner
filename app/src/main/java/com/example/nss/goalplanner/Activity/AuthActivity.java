package com.example.nss.goalplanner.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.nss.goalplanner.R;
import com.example.nss.goalplanner.SignInFragment;

import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity {

    android.support.v4.app.FragmentManager fragmentManager;

    private static final String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        fragmentManager =getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.container, SignInFragment.newInstance(null,null)).commit();

        ButterKnife.bind(this);

    }

    public void networknotworking(){

        Toast.makeText(AuthActivity.this,getString(R.string.network_not_connected),Toast.LENGTH_LONG).show();
    }
}
