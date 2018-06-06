package com.example.nss.goalplanner.Network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by NSS on 2018-05-04.
 */


public class Requestintercepter implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();

        HttpUrl url = originalHttpUrl.newBuilder()
                .addQueryParameter("Authentication", "")
                .build();

        Request request = original.newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
