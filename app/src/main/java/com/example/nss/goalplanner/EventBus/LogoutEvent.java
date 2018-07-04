package com.example.nss.goalplanner.EventBus;

public class LogoutEvent {

    public static final int NOTAVAILABLENETWORK=0;
    public static final int SUCCESS=1;
    public static final int FAIL=2;

    int state;

    public LogoutEvent(int state){
        this.state= state;
    }

    public int getState(){

        return state;
    }
}
