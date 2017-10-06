package com.demo;

import android.support.multidex.MultiDexApplication;

import com.demo.classes.ConnectivityReceiver;

public class MyApplication extends MultiDexApplication {
 
    private static MyApplication mInstance;
 
    @Override
    public void onCreate() {
        super.onCreate();
 
        mInstance = this;
    }
 
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
 
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}