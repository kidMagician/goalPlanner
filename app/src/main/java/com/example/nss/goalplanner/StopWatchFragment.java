package com.example.nss.goalplanner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nss.goalplanner.EventBus.ChronometerStartEvent;
import com.example.nss.goalplanner.EventBus.ChronometerTickEvent;
import com.example.nss.goalplanner.EventBus.GoalTotaltimeChangeEvent;
import com.example.nss.goalplanner.EventBus.SelectGoalEvent;
import com.example.nss.goalplanner.Listener.StopwatchUpdateLisenter;
import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.Service.StopwatchService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StopWatchFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.txt_goal_name)
    TextView txt_goal_name;
    @BindView(R.id.txt_start_time)
    TextView txt_start_time;
    @BindView(R.id.txt_timer)
    TextView txt_timer;
    @BindView(R.id.fab_play)
    FloatingActionButton fab_play;


    boolean isPlaying= false;

    private Goal selectedGoal;


    private static final String GOAL="goal";

    String starttimeFormat = "hh:mm:ss";

    public StopWatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StopWatchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StopWatchFragment newInstance(String param1, String param2) {
        StopWatchFragment fragment = new StopWatchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_stop_watch, container, false);

        ButterKnife.bind(this, v);

        initView();

        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedGoal !=null){

                    play();
                }else
                {
                    Toast.makeText(getActivity(),getString(R.string.stopwatch_alert_selectvoca_first),Toast.LENGTH_LONG).show();
                }


            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void play(){

        if(isPlaying){

            stop();
            isPlaying =false;

        }else{

            playing();
            isPlaying= true;

        }

    }

    private void playing(){

        Intent i = new Intent(getContext().getApplicationContext(), StopwatchService.class);

        i.putExtra(GOAL,selectedGoal);
        i.setAction(Constants.ACTION.PLAY_ACTION);

        getContext().getApplicationContext().startService(i);


    }

    private void stop(){

        fab_play.setImageResource(R.drawable.ic_media_play);
        txt_timer.setText("00:00:00");
        txt_start_time.setText("");

        Intent i = new Intent(getContext().getApplicationContext(), StopwatchService.class);

        getContext().getApplicationContext().stopService(i);

    }


    public void initView(){
        if(selectedGoal !=null){

            txt_goal_name.setText(selectedGoal.getName());

        }

    }

    @Subscribe
    public void onEvent(ChronometerTickEvent chronometerTickEvent) {

        long time = chronometerTickEvent.getTime();

        long hours = TimeUnit.MILLISECONDS.toHours(time);
        long minutes =TimeUnit.MILLISECONDS.toMinutes(time) -TimeUnit.HOURS.toMinutes(hours);
        long seconds =TimeUnit.MILLISECONDS.toSeconds(time) -TimeUnit.MINUTES.toSeconds(minutes) -TimeUnit.HOURS.toSeconds(hours);

        txt_timer.setText(String.format("%02d:%02d:%02d",hours,minutes,seconds));
    }

    @Subscribe
    public void onEvent(ChronometerStartEvent chronometerStartEvent) {

        long start_milliTime =chronometerStartEvent.getStart_time();

        setStartTime(start_milliTime);

        fab_play.setImageResource(R.drawable.ic_media_stop);


    }


    private void setStartTime(long starttime){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(starttimeFormat, Locale.KOREA);

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(starttime);

        txt_start_time.setText(String.format(simpleDateFormat.format(calendar.getTime())));

    }

    @Subscribe
    public void onEvent(SelectGoalEvent selectGoalEvent) {
        this.selectedGoal = selectGoalEvent.getSelectedGoal();

        initView();

    }
}
