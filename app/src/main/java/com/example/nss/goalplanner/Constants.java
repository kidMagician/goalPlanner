package com.example.nss.goalplanner;

public class Constants {

    public interface ACTION{
        public static String MAIN_ACTION ="com.nss.goalplanner.MAIN_ACTION";
        public static String PLAY_ACTION = "com.nss.goalplanner.action.PLAY_ACTION";
        public static String WIGET_STOPWATCH_TICK_ACTION="com.nss.goalplanner.WIGET_STOPWATCH_TICK_ACTION";
        public static String WIGET_STOPWATCH_START_ACTION="com.nss.goalplanner.WIGET_STOPWATCH_START_ACTION";
        public static String WIGET_STOPWATCH_STOP_ACTION="om.nss.goalplanner.WIGET_STOPWATCH_STOP_ACTION";
    }
    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public static String GOAL ="goal";
    public static String START_TIME="start_time";
    public static String GOAL_NAME ="goal_name";
    public static String CHROMETER_TICK_DURAION = "chrometer_tick_duration";

}
