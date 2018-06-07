package com.example.nss.goalplanner.EventBus;

public class ChronometerStartEvent {

    String goalName;
    long start_time;


    public ChronometerStartEvent(String goalName,long start_time){

        this.goalName =goalName;
        this.start_time = start_time;
    }

    public long getStart_time() {
        return start_time;
    }
    public String getGoalName(){
        return goalName;
    }
}
