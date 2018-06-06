package com.example.nss.goalplanner.Model;

import com.google.gson.annotations.SerializedName;

public class Task implements NetCache{

    public Task(){}

    @SerializedName("start_time")
    private long start_time;

    @SerializedName("duration")
    private long duration;

    @SerializedName("goal")
    Goal goal;

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
