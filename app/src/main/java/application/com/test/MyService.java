package application.com.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

    PowerManager pm;
    PowerManager.WakeLock wl;


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();
        pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");

        wl.acquire();

        Log.d("TAG:timer", "Created");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();
        wl.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();

        Log.d("TAG:timer", batteryContext());

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String batteryContext() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        String brightness = Settings.System.getString(this.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
        String timeout = Settings.System.getString(this.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT);
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        return "Battery level: " + level + "\nBrightness: " + brightness + "\nTimeout value: " +timeout;
    }
}

