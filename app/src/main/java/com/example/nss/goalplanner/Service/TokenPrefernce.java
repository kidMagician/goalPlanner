package com.example.nss.goalplanner.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

/**
 * Created by NSS on 2018-05-12.
 */

public class TokenPrefernce {

    private static final String PREF_NAME ="auth_pref";

    private static final String TOKEN_INFO = "token_info";

    private SharedPreferences pref;

    public TokenPrefernce(Context context){

        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }

    public void setTokenInfo(Tokeninfo tokenInfo){

        SharedPreferences.Editor editor = pref.edit();

        Moshi moshi = new Moshi.Builder().build();

        JsonAdapter<Tokeninfo> jsonAdapter= moshi.adapter(Tokeninfo.class);

        String strStartinfo = jsonAdapter.toJson(tokenInfo);

        editor.putString(TOKEN_INFO,strStartinfo);

        editor.apply();

    }

    public boolean isToken(){

        String strToken =pref.getString(TOKEN_INFO,null);

        if(TextUtils.isEmpty(strToken)){
            return false;
        }else{
            return true;
        }


    }

    public Tokeninfo getTokeninfo(){

        Moshi moshi = new Moshi.Builder().build();

        JsonAdapter<Tokeninfo> jsonAdapter= moshi.adapter(Tokeninfo.class);

        String strtoken=pref.getString(TOKEN_INFO,null);

        Tokeninfo tokeninfo =null;

        if(!TextUtils.isEmpty(strtoken)){

            try{
                tokeninfo=jsonAdapter.fromJson(strtoken);
            }catch (IOException e){
                e.printStackTrace();
            }

        }

        return tokeninfo;

    }

    public void deleteToken(){

        SharedPreferences.Editor editor = pref.edit();

        editor.remove(TOKEN_INFO);
        editor.apply();
    }
}
