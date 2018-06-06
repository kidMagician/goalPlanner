package com.example.nss.goalplanner.EventBus;

import com.example.nss.goalplanner.Model.Goal;

public class SelectGoalEvent {

    Goal selectedGoal;

    public SelectGoalEvent(Goal goal){

        selectedGoal = goal;
    }

    public Goal getSelectedGoal() {
        return selectedGoal;
    }
}
