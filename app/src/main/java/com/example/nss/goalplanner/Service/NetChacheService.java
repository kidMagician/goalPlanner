package com.example.nss.goalplanner.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.nss.goalplanner.BuildConfig;
import com.example.nss.goalplanner.EventBus.LogoutEvent;
import com.example.nss.goalplanner.EventBus.SelectGoalEvent;
import com.example.nss.goalplanner.EventBus.SendNetCacheEvent;
import com.example.nss.goalplanner.Model.NetCache;
import com.example.nss.goalplanner.Model.Task;
import com.example.nss.goalplanner.Network.NetCachePreference;
import com.example.nss.goalplanner.Network.Requestintercepter;
import com.example.nss.goalplanner.Network.TaskWebService;
import com.example.nss.goalplanner.Resonse.Response;
import com.example.nss.goalplanner.util.NetworkUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetChacheService extends Service {

    private static final int TIMEINTERVAL=50000;
    private static final int CONNECT_TIMEOUT_IN_MS= 3000;

    TaskWebService taskWebService;

    Handler handler = new Handler();

    NetCachePreference netCachePreference;

    private static final String TAG = "NeCacheService";

    @Override
    public void onCreate() {
        super.onCreate();

        initNetwork();

        netCachePreference = new NetCachePreference(getApplicationContext());

        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    public NetChacheService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendNetCache(1000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void initNetwork(){

        Requestintercepter requestintercepter = new Requestintercepter(getApplicationContext());

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

    Runnable runnable= new Runnable() {
        @Override
        public void run() {

            if(NetworkUtil.isConnected(getApplicationContext())){

                if(netCachePreference.isNetcache()){

                    List<NetCache> netCaches= netCachePreference.getAllNetCache();

                    for(NetCache netCache:netCaches){

                        uploadnetCache(netCache);
                    }

                }
            }



            sendNetCache(TIMEINTERVAL);
        }
    };

    private void uploadnetCache(final NetCache netCache){

        if (netCache.getClass().equals(Task.class)) {

            taskWebService.createTask((Task)netCache)
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
                                       failedResponse(throwable,netCache);
                                   }
                               }
                    );

        }else{
//          another class
        }



    }

    private void response(Response response){

        Log.d(TAG,"success upload netCache");

    }

    private void failedResponse(Throwable throwable,NetCache netCache){

        Log.d(TAG, "failedResponse: " +throwable.toString());

        netCachePreference.setNetCache(netCache);

    }

    private void sendNetCache(int delaytime){

        handler.postDelayed(runnable,delaytime);
    }

    @Subscribe
    public void onEvnet(SendNetCacheEvent sendNetCacheEvent){

        if(NetworkUtil.isConnected(getApplicationContext())){

            if(netCachePreference.isNetcache()){

                List<NetCache> netCaches= netCachePreference.getAllNetCache();

                for(NetCache netCache:netCaches){

                    uploadnetCache(netCache);
                }

            }

            if(netCachePreference.isNetcache()){

                EventBus.getDefault().post((new LogoutEvent(LogoutEvent.FAIL)));

            }else{

                EventBus.getDefault().post((new LogoutEvent(LogoutEvent.SUCCESS)));
            }

        }
        else{
            EventBus.getDefault().post((new LogoutEvent(LogoutEvent.NOTAVAILABLENETWORK)));
        }


    }

}

