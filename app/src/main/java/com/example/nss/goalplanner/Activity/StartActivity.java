package com.example.nss.goalplanner.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.nss.goalplanner.R;
import com.example.nss.goalplanner.Service.TokenPrefernce;
import com.example.nss.goalplanner.Service.Tokeninfo;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        TokenPrefernce tokenPrefernce = new TokenPrefernce(StartActivity.this);


        if(tokenPrefernce.isToken()){

            Intent i = new Intent(StartActivity.this, MainActivity.class);

            Tokeninfo tokeninfo= tokenPrefernce.getTokeninfo();

            startActivity(i);


        }else{

            Intent i = new Intent(StartActivity.this, AuthActivity.class);
            startActivity(i);

        }

        StartActivity.this.finish();



    }
}
