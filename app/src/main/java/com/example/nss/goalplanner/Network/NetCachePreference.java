package com.example.nss.goalplanner.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.solver.Cache;

import com.example.nss.goalplanner.Model.NetCache;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NetCachePreference {

    private static final String PREF_NAME = "net_chache";

    private static int cache_id=0;

    private SharedPreferences pref;

    public NetCachePreference(Context context){

        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);

    }

    public static boolean isNetcache(){

        if(cache_id >0){

            return true;

        }else{

            return false;

        }

    }

    public void setNetCache(NetCache netCache){

        Moshi moshi = new Moshi.Builder().build();

        JsonAdapter<NetCache> jsonAdapter = moshi.adapter(NetCache.class);

        String json_net = jsonAdapter.toJson(netCache);

        SharedPreferences.Editor editor = pref.edit();

        editor.putString(Long.toString(cache_id),json_net);

        editor.apply();

        cache_id++;
    }

    public NetCache getNetCache(){

        if(cache_id>0){

            Moshi moshi = new Moshi.Builder().build();

            JsonAdapter<NetCache> jsonAdapter = moshi.adapter(NetCache.class);

            String strNetCache = pref.getString(Long.toString(cache_id),null);

            NetCache netCache =null;

            try{

                netCache=jsonAdapter.fromJson(strNetCache);

            }catch (IOException e){

                e.printStackTrace();
            }

            return netCache;

        }else{

            return null;

        }

    }

    public List<NetCache> getAllNetCache(){

        ArrayList netCaches = new ArrayList<NetCache>();

        while (cache_id>0){

            getNetCache();

            netCaches.add(cache_id);
        }

        cache_id =0;

        return netCaches;
    }


}
