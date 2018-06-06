package com.example.nss.goalplanner.Activity;


import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.nss.goalplanner.GoalCreateFragment;
import com.example.nss.goalplanner.R;

public class GoalCreateActiviy extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_create_activiy);

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.container, GoalCreateFragment.newInstance(null,null)).commit();
    }
}
