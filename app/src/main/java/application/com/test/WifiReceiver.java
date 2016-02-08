package application.com.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.widget.Toast;

/**
 * Created by gr33k1718 on 07/02/2016.
 */
public class WifiReceiver extends BroadcastReceiver {
    Context context = GlobalVars.getAppContext();
    ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = conman.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    State state = info.getState();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(state == NetworkInfo.State.CONNECTED){
            long connectedTime = System.currentTimeMillis();
            Toast.makeText(context, "[connected] " + connectedTime, Toast.LENGTH_SHORT).show();

        }
        else if(state == NetworkInfo.State.DISCONNECTED ){
            long disconnectedTime = System.currentTimeMillis();
            Toast.makeText(context, "[Disconnected] " + disconnectedTime, Toast.LENGTH_SHORT).show();
        }
    }
}
