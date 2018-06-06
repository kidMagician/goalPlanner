package com.example.nss.goalplanner.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import com.example.nss.goalplanner.BuildConfig;
import com.example.nss.goalplanner.EventBus.GoalTotaltimeChangeEvent;
import com.example.nss.goalplanner.Listener.StopwatchUpdateLisenter;
import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.Model.Task;
import com.example.nss.goalplanner.R;
import com.example.nss.goalplanner.util.NetworkUtil;
import com.example.nss.goalplanner.Network.Requestintercepter;
import com.example.nss.goalplanner.Network.TaskWebService;
import com.example.nss.goalplanner.Resonse.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class StopwatchService extends Service{

    private Goal goal;
    private Task task;
    private Chronometer chronometer;
    private Boolean isPlaying =false;

    private final IBinder mBinder = new MyBinder();
    private StopwatchUpdateLisenter stopwatchUpdateLisenter;

    TaskWebService taskWebService;

    long start_time;

    private static final String GOAL="goal";
    private static final long CONNECT_TIMEOUT_IN_MS = 3000;
    private static final String TAG = "stopwatchService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        initChrometer();

        initNetwork();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startChrometer();

        goal =intent.getParcelableExtra(GOAL);

        return super.onStartCommand(intent, flags, startId);
    }


    private void initChrometer(){

        chronometer =new Chronometer(getApplicationContext());


    }

    private void initNetwork(){

        Requestintercepter requestintercepter = new Requestintercepter();

        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(CONNECT_TIMEOUT_IN_MS, TimeUnit.MILLISECONDS)
                        .addInterceptor(requestintercepter)
                        .addInterceptor(new HttpLoggingInterceptor())
                        .build();

        Retrofit retrofit =
                new Retrofit.Builder().
                        baseUrl(BuildConfig.BASE_SERVER_URL).
                        client(okHttpClient).
                        addConverterFactory(GsonConverterFactory.create()).
                        addCallAdapterFactory(RxJava2CallAdapterFactory.create()).
                        build();


        taskWebService =retrofit.create(TaskWebService.class);
    }

    private void createTask(){

        task = new Task();

        task.setGoal(goal);
        task.setStart_time(start_time);

        long end_time =SystemClock.elapsedRealtime()- chronometer.getBase();
        task.setDuration(start_time -end_time);

        stopChrometer();

        if(NetworkUtil.isConnected(getApplicationContext())){

            taskWebService.createTask(task)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Response>() {
                        @Override
                        public void accept(Response response) throws Exception {

                            response(response);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            failedResponse(throwable);
                        }
                    });
        }else{

            saveNetChache();
        }


    }

    private void response(Response response){


        if(response.getResoult_code() ==Response.SUCEES_REQUEST){

            Toast.makeText(getApplicationContext(),getString(R.string.stopwatch_toast_txt_sucess_creategoal),Toast.LENGTH_LONG).show();

            totaltimechanged();

            Log.d(TAG,"takscreate sucees");
        }

    }

    private void totaltimechanged(){

        goal.setTotal_time(goal.getTotal_time() + task.getDuration());



    }

    private void failedResponse(Throwable throwable){

        Toast.makeText(getApplicationContext(),getString(R.string.stopwatch_toast_txt_failed_creategoal),Toast.LENGTH_LONG).show();

        saveNetChache();

        Log.d(TAG,throwable.toString());
    }

    private void saveNetChache(){


    }



    @Override
    public void onDestroy() {
        if(isPlaying){
            createTask();

            isPlaying =!isPlaying;
        }


        super.onDestroy();

    }

    private void startChrometer(){

        if(!isPlaying){
            isPlaying =true;
            start_time =System.currentTimeMillis();

            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    long elapsedMillis =SystemClock.elapsedRealtime()- chronometer.getBase();
                    updateUI(elapsedMillis);
                }
            });

        }

    }

    private void stopChrometer(){

        if(!isPlaying) {
            isPlaying =false;
            chronometer.stop();
        }
    }


    private void updateUI(long time){

        if(stopwatchUpdateLisenter !=null){

            stopwatchUpdateLisenter.update(time);
        }

    }

    public class MyBinder extends Binder {
        public StopwatchService getService() {
            return StopwatchService.this;
        }
        public long getStartTime(){return StopwatchService.this.start_time;}
        public boolean isPlaying(){return StopwatchService.this.isPlaying;}
    }



    public void setStopwatchUpdateLisenter(StopwatchUpdateLisenter stopwatchUpdateLisenter) {
        this.stopwatchUpdateLisenter = stopwatchUpdateLisenter;
    }
}