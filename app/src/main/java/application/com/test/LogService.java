package application.com.test;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class LogService extends Service {

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();

        Log.d("TAG:timer", batteryContext());

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();
        wl.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String batteryContext() {
        long interactionTime;

        long networkTraffic = NetworkContext.getNetworkTraffic();
        long mobileTraffic = NetworkContext.getMobileTraffic();

        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int batteryLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        int period = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        int brightness = DisplayContext.screenBrightness();

        int timeOut = DisplayContext.screenTimeout();

        if(!pm.isInteractive()) {
            interactionTime = DisplayContext.InteractionTimer.loadTime(Constants.SCREEN_ON_TIME_PREF);
            DisplayContext.InteractionTimer.saveTime(0l,Constants.SCREEN_ON_START_TIME_PREF);
        }else {
            interactionTime = System.currentTimeMillis() - DisplayContext.InteractionTimer.loadTime(Constants.SCREEN_ON_START_TIME_PREF);
            DisplayContext.InteractionTimer.saveTime(System.currentTimeMillis(),Constants.SCREEN_ON_START_TIME_PREF);
            Log.i("[BroadcastReceiver]", "Interaction time " + interactionTime);
        }
        DisplayContext.InteractionTimer.clearTime();

        DatabaseLogger c = new DatabaseLogger(this);
        SystemContext s = new SystemContext(period,brightness,batteryLevel, timeOut, networkTraffic, mobileTraffic,interactionTime,0.0,0.0);
        //c.clearAllLogs();
        c.logStatus(s);

        restartService();

        return "Battery level: " + batteryLevel +
                "\nBrightness: " + brightness +
                "\nTimeout value: " +timeOut +
                "\nPeriod " + period +
                "\nNetwork traffic " + networkTraffic +
                "\nMobileTraffic " + mobileTraffic +
                "\nInteraction time " + interactionTime;
    }

    private void restartService(){
        stopService(new Intent(this, ScreenOnService.class));
        startService(new Intent(this,ScreenOnService.class));
    }



}

