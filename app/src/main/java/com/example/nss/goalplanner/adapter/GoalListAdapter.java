package com.example.nss.goalplanner.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nss.goalplanner.EventBus.SelectGoalEvent;
import com.example.nss.goalplanner.Listener.GoalItemChangeListner;
import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.R;
import com.example.nss.goalplanner.Service.StopwatchService;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalListAdapter extends RecyclerView.Adapter<GoalListAdapter.GoalHolder> {

    List<Goal> goals;

    Context context;

    GoalItemChangeListner goalItemChangeListner;

    GoalHolder selectedHolder;

    String myFormat = "yyyy/MM/dd";

    public GoalListAdapter(Context context, List<Goal> goals){
        this.context = context;
        this.goals = goals;
    }

    public void setGoalItemChangeListner(GoalItemChangeListner goalItemChangeListner) {
        this.goalItemChangeListner = goalItemChangeListner;
    }

    @Override
    public GoalHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_item,parent,false);

        return new GoalHolder(v);
    }

    @Override
    public void onBindViewHolder(final GoalHolder holder, final int position) {

        holder.txt_goalname.setText(goals.get(position).getName());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat,Locale.KOREA);

        calendar.setTimeInMillis(goals.get(position).getStart_date());
        holder.txt_startdate.setText(simpleDateFormat.format(calendar.getTime()));

        calendar.setTimeInMillis(goals.get(position).getEnd_date());
        holder.txt_enddate.setText(simpleDateFormat.format(calendar.getTime()));

        long milli_totaltime =goals.get(position).getTotal_time();

        long hours =TimeUnit.MILLISECONDS.toHours(milli_totaltime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milli_totaltime) - TimeUnit.HOURS.toMinutes(hours);
        holder.txt_totaltime.setText(String.format(context.getString(R.string.goallist_rv_txt_totaltime_stringformat),hours, minutes));

        holder.card_goal.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {


                                                    EventBus.getDefault().post(new SelectGoalEvent(goals.get(position)));

                                                    if(selectedHolder !=null){
                                                        selectedHolder.card_goal.setBackgroundColor(Color.WHITE);
                                                    }

                                                    holder.card_goal.setBackgroundColor(context.getColor(R.color.red));
                                                    selectedHolder = holder;
                                                }
                                            }
        );

        holder.card_goal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });



    }



    @Override
    public int getItemCount() {
        return goals.size();
    }


    public class GoalHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_goal_name)
        TextView txt_goalname;
        @BindView(R.id.txt_total_time)
        TextView txt_totaltime;
        @BindView(R.id.txt_start_date)
        TextView txt_startdate;
        @BindView(R.id.txt_end_date)
        TextView txt_enddate;
        @BindView(R.id.card_goal)
        CardView card_goal;

        public GoalHolder(View v){
            super(v);
            ButterKnife.bind(this,v);
        }

    }
}
