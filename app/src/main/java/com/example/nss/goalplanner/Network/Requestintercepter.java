package com.example.nss.goalplanner.Network;

import android.content.Context;

import com.example.nss.goalplanner.Service.TokenPrefernce;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by NSS on 2018-05-04.
 */


public class Requestintercepter implements Interceptor {

    Context context;

    public Requestintercepter(Context context){
        this.context =context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();

        TokenPrefernce tokenPrefernce = new TokenPrefernce(context);

        Request request = original.newBuilder().addHeader("Authorization","Token "+ tokenPrefernce.getTokeninfo().getToken()).build();
        return chain.proceed(request);
    }
}
