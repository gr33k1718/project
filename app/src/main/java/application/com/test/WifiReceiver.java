package application.com.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;
import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver {
    Context context = GlobalVars.getAppContext();
    ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifiInfo = conman.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    NetworkInfo mobileInfo = conman.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    State wifiState = wifiInfo.getState();
    State mobileState = mobileInfo.getState();
    private static final long TIME_ERROR = 7200000;
    long mobileDisconnected = 0;
    long mobileConnected = 0;
    long wifiTime = 0;
    long mobileTime = 0;
    long wifiConnected = 0;
    long wifiDisconnected = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        //NetworkContext.clearTime();
        //long prevTime = NetworkContext.loadTime("wifiTime");

        /*if(mobileState == NetworkInfo.State.CONNECTED){

            mobileConnected = System.currentTimeMillis();
            NetworkContext.saveTime(mobileConnected, "mobileStart");

            Toast.makeText(context, "[mobile connected] " + mobileConnected, Toast.LENGTH_SHORT).show();

        }
        else if(mobileState == NetworkInfo.State.DISCONNECTED){
            mobileDisconnected = System.currentTimeMillis();
            NetworkContext.saveTime(mobileDisconnected, "wifiStart");
            mobileTime = mobileDisconnected - NetworkContext.loadTime("mobileStart");

            if(mobileTime < TIME_ERROR) {
                NetworkContext.saveTime(mobileTime + prevTime, "wifiTime");
            }


            Toast.makeText(context, "[Mobile disconnected] " + NetworkContext.loadTime("wifiTime"), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "[No network]", Toast.LENGTH_SHORT).show();
        }
       /* if(wifiState == NetworkInfo.State.CONNECTED){
            long connectedTime = System.currentTimeMillis();
            Toast.makeText(context, "[wifi connected] " + connectedTime, Toast.LENGTH_SHORT).show();

        }
        else if(wifiState == NetworkInfo.State.DISCONNECTED ){
            long disconnectedTime = System.currentTimeMillis();
            Toast.makeText(context, "[wifi Disconnected] " + disconnectedTime, Toast.LENGTH_SHORT).show();
        }

        */if (mobileInfo.isConnected()) {
            wifiConnected = System.currentTimeMillis();
            Log.i("[WiFi receiver]", "Connected " + wifiConnected);
            Toast.makeText(context, "Wifi", Toast.LENGTH_LONG).show();
        } else {
            wifiDisconnected = System.currentTimeMillis();
            wifiTime = wifiDisconnected - wifiConnected;
            Log.i("[WiFi receiver]", "Disconnected " + wifiTime);
            Toast.makeText(context, "Time connected " + wifiTime, Toast.LENGTH_LONG).show();
        }
    }

    /*private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
                Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "Not Connected", Toast.LENGTH_LONG).show();
            }
        }
    };*/

    /*public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI)
            Log.d("[WifiReceiver]", "Have Wifi Connection");
        else
            Log.d("[WifiReceiver]", "Don't have Wifi Connection");
    }*/
}
