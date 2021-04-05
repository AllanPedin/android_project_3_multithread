package com.example.android_project_3_multithread;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityCheck {
    private Context context;

    ConnectivityCheck(Context context) {
        this.context = context;
    }

    public boolean isNetworkReachable() {
        NetworkInfo current = getNetworkInfo();
        return (current == null)?false:(current.getState() == NetworkInfo.State.CONNECTED);
    }

    public boolean isWifiReachable() {
        NetworkInfo current = getNetworkInfo();
        return (current == null)?false:(current.getType() == ConnectivityManager.TYPE_WIFI);
    }

    private NetworkInfo getNetworkInfo() {
        ConnectivityManager mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return mManager.getActiveNetworkInfo();
    }

}