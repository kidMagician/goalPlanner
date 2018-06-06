package com.example.nss.goalplanner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nss.goalplanner.Activity.MainActivity;
import com.example.nss.goalplanner.Listener.GoalSelectLisnter;
import com.example.nss.goalplanner.Listener.StopwatchUpdateLisenter;
import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.Service.StopwatchService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StopWatchFragment extends Fragment implements StopwatchUpdateLisenter,ServiceConnection,GoalSelectLisnter{
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

    StopwatchService stopwatchService;

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
                Toast.makeText(getActivity(),getString(R.string.stopwatch_toast_txt_failed_creategoal),Toast.LENGTH_LONG).show();
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
    public void onResume() {
        super.onResume();

        Intent i = new Intent(getActivity(), StopwatchService.class);

        getContext().bindService(i,this,Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onPause() {
        super.onPause();

        getContext().unbindService(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getContext().unbindService(this);

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
        getContext().getApplicationContext().startService(i);

        i = new Intent(getContext(),StopwatchService.class);

        getContext().bindService(i,this,Context.BIND_AUTO_CREATE);

    }

    private void stop(){

        fab_play.setImageResource(R.drawable.ic_media_play);
        txt_timer.setText("00:00:00");
        txt_start_time.setText("");

        Intent i = new Intent(getContext().getApplicationContext(), StopwatchService.class);

        getContext().unbindService(this);
        getContext().getApplicationContext().stopService(i);
    }


    public void initView(){
        if(selectedGoal !=null){

            txt_goal_name.setText(selectedGoal.getName());

        }

    }

    @Override
    public void update(long time) {

        long hours = TimeUnit.MICROSECONDS.toHours(time);
        long minutes =TimeUnit.MICROSECONDS.toMinutes(time) -TimeUnit.HOURS.toMinutes(hours);
        long seconds =TimeUnit.MICROSECONDS.toSeconds(time) -TimeUnit.MINUTES.toSeconds(minutes) -TimeUnit.HOURS.toSeconds(hours);

        txt_timer.setText(String.format("%02d:%02d",hours,minutes,seconds));
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        StopwatchService.MyBinder binder= (StopwatchService.MyBinder)iBinder;

        if(((StopwatchService.MyBinder) iBinder).isPlaying()){

            stopwatchService=binder.getService();
            stopwatchService.setStopwatchUpdateLisenter(this);

            long start_milliTime =binder.getStartTime();

            setStartTime(start_milliTime);

            fab_play.setImageResource(R.drawable.ic_media_stop);

        }else{

            getContext().unbindService(this);
        }



    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

        stopwatchService =null;

    }

    private void setStartTime(long starttime){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(starttimeFormat, Locale.KOREA);

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(starttime);

        txt_start_time.setText(String.format(simpleDateFormat.format(calendar.getTime())));

    }

    @Override
    public void setSelectedGoal(Goal selectedGoal) {
        this.selectedGoal = selectedGoal;

        initView();

    }
}
