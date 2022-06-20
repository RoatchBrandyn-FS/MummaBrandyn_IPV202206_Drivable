package com.example.drivable.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean isConnected(Context context){
        ConnectivityManager cMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cMgr !=null){
            NetworkInfo info = cMgr.getActiveNetworkInfo();

            if (info != null) {
                return info.isConnected();
            }
        }

        return false;
    }

}
