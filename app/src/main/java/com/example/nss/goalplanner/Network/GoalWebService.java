package com.example.nss.goalplanner.Network;

import com.example.nss.goalplanner.Model.Goal;
import com.example.nss.goalplanner.Model.GoalWarpper;
import com.example.nss.goalplanner.Resonse.Response;
import com.example.nss.goalplanner.Resonse.ResponseGoalCreate;
import com.example.nss.goalplanner.Resonse.ResponseGoalUpdate;


import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by NSS on 2018-05-14.
 */

public interface GoalWebService {

    @GET("/goal/start")
    Observable<Response> startRequest();

    @GET("/goal")
    Observable<GoalWarpper> getallGoal();

    @POST("/goal")
    Observable<ResponseGoalCreate> createGoal(@Body Goal goal);

    @DELETE("/goal")
    Observable<Response> deleteGoal(@Body Goal goal);

    @PUT("/goal")
    Observable<ResponseGoalUpdate> updateGoal(@Body Goal goal);


}
