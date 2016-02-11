package application.com.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

public class BatteryService extends Service {

    PowerManager pm;
    PowerManager.WakeLock wl;

    private final String RX_FILE = "/sys/class/net/wlan0/statistics/rx_bytes";
    private final String TX_FILE = "/sys/class/net/wlan0/statistics/tx_bytes";


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
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        long interactionTime;
        long prevMobileStats = NetworkContext.loadTraffic(Constants.MOBILE_TRAFFIC);
        long prevNetworkStats = NetworkContext.loadTraffic(Constants.NETWORK_TRAFFIC);

        long currentMobileStats = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
        long currentNetworkStats = readFile(TX_FILE) + readFile(RX_FILE);

        NetworkContext.saveTraffic(currentNetworkStats,Constants.NETWORK_TRAFFIC);
        NetworkContext.saveTraffic(currentMobileStats,Constants.MOBILE_TRAFFIC);

        long networkTraffic = currentNetworkStats - prevNetworkStats;
        long mobileTraffic = currentMobileStats - prevMobileStats;

        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int batteryLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        int period = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        int brightness = DisplayContext.screenBrightness();

        int timeOut = DisplayContext.screenTimeout();

        if(!pm.isInteractive()) {
            interactionTime = DisplayContext.InteractionTimer.loadTime(Constants.SCREEN_ON_TIME_PREF);
        }else {
            interactionTime = System.currentTimeMillis() - DisplayContext.InteractionTimer.loadTime(Constants.SCREEN_ON_START_TIME_PREF);
            DisplayContext.InteractionTimer.saveTime(System.currentTimeMillis(),Constants.SCREEN_ON_START_TIME_PREF);
            Log.i("[BroadcastReceiver]", "Interaction time " + interactionTime);
        }
        DisplayContext.InteractionTimer.clearTime();

        SystemContext s = new SystemContext(period,brightness,batteryLevel, timeOut, networkTraffic, mobileTraffic,interactionTime,0.0,0.0);
        ContextLogger c = new ContextLogger(this);
        //c.clearAllLogs();
        c.logStatus(s);

        stopService(new Intent(this, ScreenOnService.class));
        startService(new Intent(this,ScreenOnService.class));

        return "Battery level: " + batteryLevel +
                "\nBrightness: " + brightness +
                "\nTimeout value: " +timeOut +
                "\nPeriod " + period +
                "\nNetwork traffic " + networkTraffic +
                "\nMobileTraffic " + mobileTraffic +
                "\nInteraction time " + interactionTime;
    }

    private long readFile(String fileName){
        File file = new File(fileName);
        BufferedReader br = null;
        long bytes = 0;
        try{
            br = new BufferedReader(new FileReader(file));
            String line = "";
            line = br.readLine();
            bytes = Long.parseLong(line);
        }  catch (Exception e){
            e.printStackTrace();
            return 0;

        } finally{
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return bytes;
    }
}

