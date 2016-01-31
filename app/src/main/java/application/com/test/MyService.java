package application.com.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by gr33k1718 on 14/01/2016.
 */
public class MyService extends Service {

    int a = 0;
    IntentFilter level = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    private final BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (! Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) return;


        }
    };
    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(mBatteryInfoReceiver,level);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        IntentFilter level = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        Intent i = GlobalVars.getAppContext().registerReceiver(null, level);
        final int battery_level = i.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        BroadcastReceiver bcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                a = a +1;

                Toast.makeText(GlobalVars.getAppContext(), "Started " + battery_level + " " + a, Toast.LENGTH_SHORT).show();
            }
        };

        registerReceiver(bcast, filter);

        //Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();*/

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
