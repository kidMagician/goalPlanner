package com.example.nss.goalplanner.Resonse;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NSS on 2018-05-05.
 */

public class Responsesignup {

    static public final int FAIL_SIGNUP =0;
    static public final int SUCEES_SIGNUP =1;
    static public final int USERNAM_DUPLICATION= 2;
    static public final int EMAIL_NOT_EXIST =3;
    static public final int EMAIL_DUPLICATION=4;
    static public final int DUPLICATION_USERNAME_EMIL=6;

    @SerializedName("result")
    private int result_signup;

    public int getResult_signup() {
        return result_signup;
    }

    public void setResult_signup(int result_signup) {
        this.result_signup = result_signup;
    }






}
