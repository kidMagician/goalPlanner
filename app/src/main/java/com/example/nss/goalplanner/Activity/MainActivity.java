package com.example.nss.goalplanner.Activity;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.nss.goalplanner.GoalListFragment;
import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.Network.TaskWebService;
import com.example.nss.goalplanner.R;
import com.example.nss.goalplanner.Service.TokenPrefernce;
import com.example.nss.goalplanner.StopWatchFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public TaskWebService taskWebService;

    int screen_state;

    private static final int SERVER_WRONG =0;
    private static final int NETWORK_NOT_AVAILABLE=1;
    private static final int LAYOUT_OK=2;

    @BindView(R.id.layout_no_network)
    View layout_no_network;
    @BindView(R.id.layout_bad_server)
    View layout_server_wrong;
    @BindView(R.id.layout_ok)
    View layout_ok;
    @BindView(R.id.txt_bad_server_retry)
    TextView txt_bad_server_retry;
    @BindView(R.id.txt_no_network_retry)
    TextView txt_no_network_retry;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation_drawer)
    NavigationView navigration_drawer;

    private final String FRAGMENT_GOALLIST_TAG = "fragment_goalList";
    private final String FRAGMENT_SOTPWATCH_TAG ="fragment_stopwatch";

    FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initFragment();

        initView();

        initToolbar();

        initNav();


        txt_bad_server_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fragmentManager.findFragmentByTag(FRAGMENT_GOALLIST_TAG) !=null){
                    ((GoalListFragment)fragmentManager.findFragmentByTag(FRAGMENT_GOALLIST_TAG)).getAllGoal();
                    OKlayout();
                }

            }
        });

        txt_no_network_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fragmentManager.findFragmentByTag(FRAGMENT_GOALLIST_TAG) !=null){
                    ((GoalListFragment)fragmentManager.findFragmentByTag(FRAGMENT_GOALLIST_TAG)).getAllGoal();
                    OKlayout();
                }

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case(R.id.nav_analystic):


                break;

            case(R.id.nav_setting):

                Intent i = new Intent(this,SettingActivity.class);

                startActivity(i);

                break;

            case(R.id.nav_signout):

                signout();

                break;
        }

        return true;
    }

    private void signout(){

        TokenPrefernce tokenPrefernce = new TokenPrefernce(this);

        tokenPrefernce.deleteToken();

        Intent i = new Intent(this, AuthActivity.class);

        startActivity(i);

        this.finish();

    }

    private void initNav(){

        View headerView =navigration_drawer.getHeaderView(0);

        navigration_drawer.setNavigationItemSelectedListener(this);

        TextView txt_header_username = headerView.findViewById(R.id.txt_hearder_username);

        TokenPrefernce tokenPrefernce = new TokenPrefernce(this);
        txt_header_username.setText(tokenPrefernce.getTokeninfo().getUsername());

    }

    private void initToolbar(){

        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }


    private void initView(){

        OKlayout();
    }

    private void OKlayout(){

        screen_state=LAYOUT_OK;

        layout_ok.setVisibility(View.VISIBLE);
        layout_no_network.setVisibility(View.GONE);
        layout_server_wrong.setVisibility(View.GONE);
    }

    public void networknotworking(){

        screen_state = NETWORK_NOT_AVAILABLE;

        layout_no_network.setVisibility(View.VISIBLE);
        layout_ok.setVisibility(View.GONE);
        layout_server_wrong.setVisibility(View.GONE);

    }
    public void serverWrong(){

        screen_state = SERVER_WRONG;

        layout_server_wrong.setVisibility(View.VISIBLE);
        layout_no_network.setVisibility(View.GONE);
        layout_ok.setVisibility(View.GONE);


    }


    private void initFragment(){

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.listFragment, GoalListFragment.newInstance(null,null),FRAGMENT_GOALLIST_TAG).commit();

        FragmentTransaction mfragmentTransaction= fragmentManager.beginTransaction();
        mfragmentTransaction.replace(R.id.stopwatchfragment, StopWatchFragment.newInstance(null,null),FRAGMENT_SOTPWATCH_TAG).commit();

    }


    public void setSelectedGoal(Goal selectedGoal) {

        if(fragmentManager.findFragmentByTag(FRAGMENT_SOTPWATCH_TAG)!=null){

            ((StopWatchFragment)fragmentManager.findFragmentByTag(FRAGMENT_SOTPWATCH_TAG)).initView();

        }
    }
}
