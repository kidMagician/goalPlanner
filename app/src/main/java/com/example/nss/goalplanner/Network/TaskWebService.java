package com.example.nss.goalplanner.Network;

import com.example.nss.goalplanner.Model.Task;
import com.example.nss.goalplanner.Resonse.Response;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface TaskWebService {

    @POST("/goal/task")
    Observable<Response> createTask(@Body Task task);



}
