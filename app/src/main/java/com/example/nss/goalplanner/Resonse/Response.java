package com.example.nss.goalplanner.Resonse;

import com.google.gson.annotations.SerializedName;

public class Response {

    static public final int FAIL_REQUEST =0;
    static public final int SUCEES_REQUEST =1;

    @SerializedName("result_code")
    int resoult_code;

    public int getResoult_code() {
        return resoult_code;
    }

    public void setResoult_code(int resoult_code) {
        this.resoult_code = resoult_code;
    }
}
