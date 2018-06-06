package com.example.nss.goalplanner.EventBus;

public class GoalTotaltimeChangeEvent {

    long duraion;

    public GoalTotaltimeChangeEvent(long duraion){

        this.duraion = duraion;

    }

    public long getDuraion() {
        return duraion;
    }
}
