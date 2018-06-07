package com.example.nss.goalplanner.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import com.example.nss.goalplanner.Activity.MainActivity;
import com.example.nss.goalplanner.BuildConfig;
import com.example.nss.goalplanner.Constants;
import com.example.nss.goalplanner.EventBus.ChronometerStartEvent;
import com.example.nss.goalplanner.EventBus.ChronometerStopEvent;
import com.example.nss.goalplanner.EventBus.ChronometerTickEvent;
import com.example.nss.goalplanner.EventBus.GoalTotaltimeChangeEvent;
import com.example.nss.goalplanner.Listener.StopwatchUpdateLisenter;
import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.Model.Task;
import com.example.nss.goalplanner.R;
import com.example.nss.goalplanner.util.NetworkUtil;
import com.example.nss.goalplanner.Network.Requestintercepter;
import com.example.nss.goalplanner.Network.TaskWebService;
import com.example.nss.goalplanner.Resonse.Response;
import com.example.nss.goalplanner.wiget.StopwatchWiget;

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
    static public Boolean isPlaying =false;

    TaskWebService taskWebService;

    long start_time;

    private static final String GOAL="goal";
    private static final long CONNECT_TIMEOUT_IN_MS = 3000;
    private static final String TAG = "stopwatchService";


    Handler handler =new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        initNetwork();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction().equals(Constants.ACTION.PLAY_ACTION)){
            if(!isPlaying){

                goal =intent.getParcelableExtra(GOAL);

                startChrometer();

//                showNotification();

            }else {

                createTask();

                stopChrometer();

                EventBus.getDefault().post(new ChronometerStopEvent());
                notifyWigdetServiceStop();

//                stopForeground(true);

                stopSelf();
            }


        }else {

            stopSelf();
        }


        return super.onStartCommand(intent, flags, startId);
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

        task.setGoalname(goal.getName());
        task.setStart_time(start_time);

        long duration=SystemClock.elapsedRealtime()- chronometer.getBase();
        task.setDuration(duration);

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

        EventBus.getDefault().post(new GoalTotaltimeChangeEvent(task.getDuration()));

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

        super.onDestroy();

    }

    private void startChrometer(){

        isPlaying =true;
        start_time =System.currentTimeMillis();

        chronometer = new Chronometer(getApplicationContext());
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        updateUI();

        EventBus.getDefault().post(new ChronometerStartEvent(goal.getName(),start_time));

        notifyWigdetServiceStart(start_time);

    }

    private void stopChrometer(){

        if(isPlaying) {
            isPlaying =false;
            chronometer.stop();
            handler.removeCallbacks(runnable);
        }
    }


    private void showNotification(){

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent playIntent = new Intent(this, StopwatchService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_media_play);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(goal.getName())
                .setTicker(goal.getName())
                .setContentText("My song")
                .setSmallIcon(R.drawable.ic_media_play)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_play, "Play",
                        pplayIntent).build();

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            long elapsedMillis =SystemClock.elapsedRealtime()- chronometer.getBase();
            EventBus.getDefault().post(new ChronometerTickEvent(elapsedMillis));

            notifyWigfetChrometerTick(elapsedMillis);
            updateUI();


        }
    };

    private void updateUI(){

        handler.postDelayed(runnable,1000);

    }

    private void notifyWigdetServiceStart(long start_time){
        if(StopwatchWiget.is_enable) {
            Intent i = new Intent(getApplicationContext(), StopwatchWiget.class);
            i.setAction(Constants.ACTION.WIGET_STOPWATCH_START_ACTION);
            i.putExtra(Constants.START_TIME, start_time);
            i.putExtra(Constants.GOAL_NAME, goal.getName());

            sendBroadcast(i);
        }

    }

    private void notifyWigfetChrometerTick(long duration){

        if(StopwatchWiget.is_enable) {
            Intent i = new Intent(getApplicationContext(), StopwatchWiget.class);
            i.setAction(Constants.ACTION.WIGET_STOPWATCH_TICK_ACTION);
            i.putExtra(Constants.CHROMETER_TICK_DURAION, duration);

            sendBroadcast(i);
        }

    }

    private void notifyWigdetServiceStop(){

        if(StopwatchWiget.is_enable){
            Intent i = new Intent(getApplicationContext(), StopwatchWiget.class);
            i.setAction(Constants.ACTION.WIGET_STOPWATCH_STOP_ACTION);

            sendBroadcast(i);

        }



    }

}