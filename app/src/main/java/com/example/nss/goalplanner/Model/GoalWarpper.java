package com.example.nss.goalplanner.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by NSS on 2018-05-14.
 */

public class GoalWarpper{

    @SerializedName("results")
    private List<Goal> goals;

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }



}
