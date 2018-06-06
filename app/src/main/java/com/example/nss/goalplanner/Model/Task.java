package com.example.nss.goalplanner.Model;

import com.google.gson.annotations.SerializedName;

import io.reactivex.annotations.Nullable;

public class Task implements NetCache{

    public Task(){}

    @SerializedName("starttime")
    private long start_time;

    @SerializedName("duration")
    private long duration;

    @SerializedName("goalname")
    @Nullable
    private String goalname;

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

    public String getGoalname() {
        return goalname;
    }

    public void setGoalname(String goalname) {
        this.goalname = goalname;
    }
}
