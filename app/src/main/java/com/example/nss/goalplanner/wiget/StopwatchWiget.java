package com.example.nss.goalplanner.wiget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.nss.goalplanner.Activity.MainActivity;
import com.example.nss.goalplanner.Constants;
import com.example.nss.goalplanner.EventBus.ChronometerStartEvent;
import com.example.nss.goalplanner.EventBus.ChronometerTickEvent;
import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.R;
import com.example.nss.goalplanner.Service.StopwatchService;
import com.example.nss.goalplanner.Service.TokenPrefernce;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of App Widget functionality.
 */
public class StopwatchWiget extends AppWidgetProvider {

    Goal goal;

    private static final String GOAL="goal";

    static public boolean is_enable=false;
    static RemoteViews views;
    static AppWidgetManager mAppWidgetManager;
    static int mAppWidgetId;
    static Context mContext;

    String starttimeFormat = "hh:mm:ss";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.stopwatch_wiget);
        mAppWidgetManager =appWidgetManager;
        mAppWidgetId =appWidgetId;
        mContext =context;

        TokenPrefernce tokenPrefernce =new TokenPrefernce(context);

        if(tokenPrefernce.isToken()){

            Intent intent = new Intent(context,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

            views.setOnClickPendingIntent(R.id.btn_play,pendingIntent);
        }else{

            Toast.makeText(context,context.getString(R.string.widget_toast_not_authenficated),Toast.LENGTH_LONG).show();
        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        is_enable =true;
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled

        is_enable =false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals(Constants.ACTION.WIGET_STOPWATCH_START_ACTION)){

            long start_milliTime = intent.getLongExtra(Constants.START_TIME,0);

            setStartTime(start_milliTime);

            String goal_name= intent.getStringExtra(Constants.GOAL_NAME);
            views.setTextViewText(R.id.txt_goal_name,goal_name);

            views.setImageViewResource(R.id.btn_play,R.drawable.ic_media_stop);

            Intent i = new Intent(context,StopwatchService.class);
            i.setAction(Constants.ACTION.PLAY_ACTION);
            PendingIntent pendingIntent = PendingIntent.getService(context,0,i,0);
            views.setOnClickPendingIntent(R.id.btn_play,pendingIntent);

            mAppWidgetManager.updateAppWidget(mAppWidgetId, views);

        }else if(intent.getAction().equals(Constants.ACTION.WIGET_STOPWATCH_TICK_ACTION)){

            long time = intent.getLongExtra(Constants.CHROMETER_TICK_DURAION,0);

            long hours = TimeUnit.MILLISECONDS.toHours(time);
            long minutes =TimeUnit.MILLISECONDS.toMinutes(time) -TimeUnit.HOURS.toMinutes(hours);
            long seconds =TimeUnit.MILLISECONDS.toSeconds(time) -TimeUnit.MINUTES.toSeconds(minutes) -TimeUnit.HOURS.toSeconds(hours);

            views.setTextViewText(R.id.txt_timer, String.format("%02d:%02d:%02d",hours,minutes,seconds));

            mAppWidgetManager.updateAppWidget(mAppWidgetId, views);


        }else if(intent.getAction().equals(Constants.ACTION.WIGET_STOPWATCH_STOP_ACTION)){

            views.setImageViewResource(R.id.btn_play,R.drawable.ic_media_play);
            views.setTextViewText(R.id.txt_goal_name,"");
            views.setTextViewText(R.id.txt_start_time,"");
            views.setTextViewText(R.id.txt_timer,context.getString(R.string.stopwatch_txt_time));

            TokenPrefernce tokenPrefernce =new TokenPrefernce(context);

            if(tokenPrefernce.isToken()){

                Intent i = new Intent(context,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);

                views.setOnClickPendingIntent(R.id.btn_play,pendingIntent);
            }else{

                Toast.makeText(context,context.getString(R.string.widget_toast_not_authenficated),Toast.LENGTH_LONG).show();
            }


            mAppWidgetManager.updateAppWidget(mAppWidgetId, views);

        }
    }


    private void setStartTime(long starttime){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(starttimeFormat, Locale.KOREA);

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(starttime);

        views.setTextViewText(R.id.txt_start_time,String.format(simpleDateFormat.format(calendar.getTime())));

    }
}

