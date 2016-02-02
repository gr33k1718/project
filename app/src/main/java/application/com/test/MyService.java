package application.com.test;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();

        Log.d("TAG:timer", "Created");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();

        Log.d("TAG:timer", String.valueOf(getBatteryLevel()));
        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        return level;
    }
}

