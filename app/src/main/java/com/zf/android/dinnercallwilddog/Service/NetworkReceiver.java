package com.zf.android.dinnercallwilddog.Service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by zifeifeng on 7/12/17.
 */

public class NetworkReceiver extends BroadcastReceiver {
    private MyNetworkCallback mNetworkCallback;
    private Context mContext;
    private final String TAG= "Network Receiver";

    public NetworkReceiver(){

    }
    public NetworkReceiver(Context mContext){
        mNetworkCallback = (MyNetworkCallback) mContext;
        this.mContext = mContext;
    }
    public interface MyNetworkCallback{
        void hasNetWork();
        void loseNetWork();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "receive "+intent.getAction());
//        IntentService intentService = new IntentService() {
//            @Override
//            protected void onHandleIntent(Intent intent) {
//
//            }
//        };
        //intentService.sendBroadcast
//        if(isNetworkAvailable()){
//            Log.e(TAG, "Yes to network");
//            mNetworkCallback.hasNetWork();
//        }
//        else{
//            Log.e(TAG, "NO to network");
//            mNetworkCallback.loseNetWork();
//        }
    }
    public boolean isNetworkAvailable() {
        if(mContext==null){

        }
        ConnectivityManager cm =
                (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
