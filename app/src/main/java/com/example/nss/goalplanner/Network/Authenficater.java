package com.example.nss.goalplanner.Network;

import com.example.nss.goalplanner.Model.Authinfo;
import com.example.nss.goalplanner.Resonse.Responsesignin;
import com.example.nss.goalplanner.Resonse.Responsesignup;


import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by NSS on 2018-05-04.
 */



public interface Authenficater {

    @POST("/auth/signin")
    Observable<Responsesignin> signIn(@Body Authinfo authinfo);

    @POST("/auth/signup")
    Observable<Responsesignup> signup(@Body Authinfo authinfo);

    @POST("/auth/findpass")
    Observable<String> findpass(@Body Authinfo authinfo);



}
