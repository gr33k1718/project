package application.com.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class ScreenOnService extends Service {

    private static long TIME_ERROR = 3600000;

    private long screenOnTime = 0;
    private long startTimer   = 0;
    private long endTimer     = 0;

    private ArrayList<Long> screenTimer;
    private SharedPreference sharedPreference = new SharedPreference();

    @Override
    public void onCreate() {
        super.onCreate();
        screenTimer = new ArrayList<>();
        long savedTime = sharedPreference.loadTimes();

        createScreenTimer();

        if(savedTime > 0){
            screenTimer.add(savedTime);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        long savedTime = sharedPreference.loadTimes();
        Toast.makeText(this, "Destroyed " + savedTime, Toast.LENGTH_SHORT).show();
        startService(new Intent(this, ScreenOnService.class));

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static String convertMillisecondsToHMmSs(long milliseconds) {
        int s = (int) (milliseconds / 1000) % 60 ;
        int m = (int) ((milliseconds / (1000*60)) % 60);
        int h   = (int) ((milliseconds / (1000*60*60)) % 24);
        return String.format("%02d:%02d:%02d", h,m,s);
    }

    public long sumList(ArrayList<Long> list){
        long sum = 0;

        for(Long l : list){
            sum += l;
        }
        return sum;
    }


    public void createScreenTimer(){
        BroadcastReceiver mybroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("[BroadcastReceiver]", "MyReceiver");

                if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                    startTimer = System.currentTimeMillis();
                    Toast.makeText(context, "Screen on " + convertMillisecondsToHMmSs(sumList(screenTimer)), Toast.LENGTH_SHORT).show();
                    Log.i("[BroadcastReceiver]", "Screen ON " + startTimer);
                }
                else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    endTimer = System.currentTimeMillis();
                    screenOnTime = endTimer - startTimer;

                    if(screenOnTime < TIME_ERROR) {
                        screenTimer.add(screenOnTime);
                        sharedPreference.saveTimes(sumList(screenTimer));
                        Log.i("[BroadcastReceiver]", "Screen OFF " + screenOnTime);
                        Log.i("[BroadcastReceiver]", "Total time " + convertMillisecondsToHMmSs(sumList(screenTimer)));
                    }

                }

            }
        };
        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }
}
