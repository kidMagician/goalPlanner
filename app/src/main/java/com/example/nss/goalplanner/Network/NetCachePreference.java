package com.example.nss.goalplanner.Network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nss.goalplanner.Model.NetCache;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class NetCachePreference {

    private static final String PREF_NAME = "net_chache";

    private SharedPreferences pref;

    public NetCachePreference(Context context){

        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }

    public void setNetCache(NetCache netCache){

        Moshi moshi = new Moshi.Builder().build();

        JsonAdapter<NetCache> jsonAdapter = moshi.adapter(NetCache.class);

        String json_net = jsonAdapter.toJson(netCache);

        SharedPreferences.Editor editor = pref.edit();

        editor.apply();

    }


}
