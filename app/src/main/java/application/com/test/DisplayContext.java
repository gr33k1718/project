package application.com.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class DisplayContext {
    private static final String SCREEN_TIME_PREFS = "SCRREN_TIME";
    private static final String SCREEN_ON_TIME = "Screen_on_time";
    private static final long TIME_ERROR = 7200000;


    private static Context context = GlobalVars.getAppContext();

    public static String screenBrightness(){
        return Settings.System.getString(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
    }

    public static String screenTimeout(){
        return Settings.System.getString(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
    }

    public static class InteractionTimer {

        private static long screenOnStartTime = 0;
        private static long screenOnEndTime = 0;
        private static long screenOnTime = 0;

        public static BroadcastReceiver setupTimer(){
            BroadcastReceiver mybroadcast = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i("[BroadcastReceiver]", "MyReceiver");
                    long prevTime = loadTime();
                    if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                        screenOnStartTime = System.currentTimeMillis();

                        Log.i("[BroadcastReceiver]", "Screen ON " + screenOnStartTime);
                        Toast.makeText(context, "Total time " + convertMillisecondsToHMmSs(loadTime()), Toast.LENGTH_SHORT).show();
                    }
                    else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                        screenOnEndTime = System.currentTimeMillis();
                        screenOnTime = screenOnEndTime - screenOnStartTime;

                        if(screenOnTime < TIME_ERROR && prevTime > 0) {
                            saveTime(prevTime + screenOnTime);

                            Log.i("[BroadcastReceiver]", "Screen OFF " + prevTime);
                            Log.i("[BroadcastReceiver]", "Total time " + convertMillisecondsToHMmSs(loadTime()));
                        }

                    }

                }
            };
            context.registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
            context.registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));

            return mybroadcast;
        }

        private static void saveTime(Long time){
            SharedPreferences settings;
            Editor editor;

            settings = context.getSharedPreferences(SCREEN_TIME_PREFS, Context.MODE_PRIVATE);
            editor = settings.edit();

            editor.putLong(SCREEN_ON_TIME, time);

            editor.commit();
        }

        private static long loadTime(){
            SharedPreferences settings;
            settings = context.getSharedPreferences(SCREEN_TIME_PREFS, Context.MODE_PRIVATE);

            return settings.getLong(SCREEN_ON_TIME, -1);
        }

        public static void clearTime(){
            SharedPreferences settings;
            Editor editor;

            settings = context.getSharedPreferences(SCREEN_TIME_PREFS, Context.MODE_PRIVATE);
            editor = settings.edit();
            editor.remove(SCREEN_ON_TIME);
            editor.commit();
        }

        private static String convertMillisecondsToHMmSs(long milliseconds) {
            int s = (int) (milliseconds / 1000) % 60 ;
            int m = (int) ((milliseconds / (1000*60)) % 60);
            int h   = (int) ((milliseconds / (1000*60*60)) % 24);
            return String.format("%02d:%02d:%02d", h, m, s);
        }
    }
}
