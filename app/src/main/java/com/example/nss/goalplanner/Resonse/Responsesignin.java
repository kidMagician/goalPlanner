package com.example.nss.goalplanner.Resonse;

import com.example.nss.goalplanner.Service.Tokeninfo;
import com.google.gson.annotations.SerializedName;

/**
 * Created by NSS on 2018-05-12.
 */

public class Responsesignin {

    static public final int FAIL_SIGNUP =0;
    static public final int SUCEES_SIGNUP =1;

    @SerializedName("result_code")
    int resoult_code;
    @SerializedName("tokeninfo")
    Tokeninfo tokeninfo;

    public Tokeninfo getTokeninfo() {
        return tokeninfo;
    }

    public void setTokeninfo(Tokeninfo tokeninfo) {
        this.tokeninfo = tokeninfo;
    }

    public int getResoult_code() {
        return resoult_code;
    }

    public void setResoult_code(int resoult_code) {
        this.resoult_code = resoult_code;
    }




}
