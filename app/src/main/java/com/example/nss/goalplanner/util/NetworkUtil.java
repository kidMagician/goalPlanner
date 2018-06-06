package com.example.nss.goalplanner.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by NSS on 2018-05-07.
 */

public class NetworkUtil {

    static public boolean isConnected(Context context){

        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();

        if(networkInfo !=null){

            if(networkInfo.isConnected()){

                return  true;
            }
        }

        return false;
    }
}
