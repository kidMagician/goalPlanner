package com.example.nss.goalplanner.Service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NSS on 2018-05-12.
 */

public class Tokeninfo  {

    @SerializedName("username")
    private  String username;
    @SerializedName("token")
    private  String token;

    public  String getUsername() {
        return username;
    }

    public  void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public  void setToken(String token) {
        this.token = token;
    }


}
