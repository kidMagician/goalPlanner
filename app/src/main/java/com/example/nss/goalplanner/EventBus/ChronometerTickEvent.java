package com.example.nss.goalplanner.EventBus;

public class ChronometerTickEvent {

    long time;

    public ChronometerTickEvent(long time){
        this.time =time;
    }

    public long getTime() {
        return time;
    }
}
