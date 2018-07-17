package com.example.nss.goalplanner.Activity;


import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.nss.goalplanner.GoalCreateFragment;
import com.example.nss.goalplanner.GoalUPdateFragment;
import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.R;

public class GoalCreateActiviy extends AppCompatActivity {

    static final String GOAL="goal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_create_activiy);

        Intent i = getIntent();

        Goal goal = i.getParcelableExtra(GOAL);

        if(goal !=null){

            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.container, GoalUPdateFragment.newInstance(goal)).commit();

        }else{

            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.container, GoalCreateFragment.newInstance(null,null)).commit();

        }


    }
}
