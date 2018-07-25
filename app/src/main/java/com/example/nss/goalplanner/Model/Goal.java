package com.example.nss.goalplanner.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by NSS on 2018-05-14.
 */

public class Goal implements Parcelable,NetCache {


    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("startdate")
    private long start_date;

    @SerializedName("totaltime")
    private long total_time;

    @SerializedName("reason")
    private String reason;

    @SerializedName("enddate")
    private long end_date;

    public void setGoal(Goal goal){

        this.id =goal.getId();
        this.name = goal.getName();
        this.total_time = goal.getTotal_time();
        this.reason = goal.getReason();
        this.start_date = goal.getStart_date();
        this.end_date =goal.getEnd_date();

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getEnd_date() {
        return end_date;
    }

    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }

    public long getTotal_time() {
        return total_time;
    }

    public void setTotal_time(long total_time) {
        this.total_time = total_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStart_date() {
        return start_date;
    }

    public void setStart_date(long start_date) {
        this.start_date = start_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(getId());
        parcel.writeString(getName());
        parcel.writeLong(getStart_date());
        parcel.writeLong(getEnd_date());
        parcel.writeString(getReason());
        parcel.writeLong(getTotal_time());

    }

    public Goal(Parcel in){

        this.id =in.readLong();
        this.name =in.readString();
        this.start_date =in.readLong();
        this.end_date = in.readLong();
        this.reason = in.readString();
        this.total_time = in.readLong();
    }

    public Goal(){

    }

    public static final Creator<Goal> CREATOR = new Creator<Goal>() {
        @Override
        public Goal createFromParcel(Parcel parcel) {
            return new Goal(parcel);
        }

        @Override
        public Goal[] newArray(int i) {
            return new Goal[i];
        }
    };
}
