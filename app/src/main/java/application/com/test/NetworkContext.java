package application.com.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkContext {

    private static Context context = GlobalVars.getAppContext();


    public static void saveTraffic(Long traffic, int type){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(Constants.NETWORK_PREFS, Context.MODE_PRIVATE);
        editor = settings.edit();

        switch (type) {
            case Constants.MOBILE_TRAFFIC:
                editor.putLong(Constants.MOBILE_TRAFFIC_PREF, traffic);
                break;
            case Constants.NETWORK_TRAFFIC:
                editor.putLong(Constants.NETWORK_TRAFFIC_PREF, traffic);
                break;
        }


        editor.commit();
    }


    public static long loadTraffic(int type){
        SharedPreferences settings;
        settings = context.getSharedPreferences(Constants.NETWORK_PREFS, Context.MODE_PRIVATE);

        switch (type){
            case Constants.NETWORK_TRAFFIC:
                return settings.getLong(Constants.NETWORK_TRAFFIC_PREF, 0);
            case Constants.MOBILE_TRAFFIC:
                return settings.getLong(Constants.MOBILE_TRAFFIC_PREF, 0);
        }

        return 0;
    }

    public static void clearTraffic(){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(Constants.NETWORK_PREFS, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(Constants.MOBILE_TRAFFIC_PREF);
        editor.remove(Constants.NETWORK_TRAFFIC_PREF);
        editor.commit();
    }
}
