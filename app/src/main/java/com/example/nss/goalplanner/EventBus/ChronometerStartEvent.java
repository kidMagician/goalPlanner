package com.example.nss.goalplanner.EventBus;

public class ChronometerStartEvent {

    long start_time;

    public ChronometerStartEvent(long start_time){
        this.start_time = start_time;
    }

    public long getStart_time() {
        return start_time;
    }
}
