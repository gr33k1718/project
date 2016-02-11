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

    private static final long TIME_ERROR = 7200000;


    private static Context context = GlobalVars.getAppContext();

    public static int screenBrightness(){
        return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
    }

    public static int screenTimeout(){
        return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
    }

    public static class InteractionTimer {

        private static long screenOnStartTime = 0;
        private static long screenOnEndTime = 0;
        private static long screenOnTime = 0;

        public static BroadcastReceiver setupTimer(){
            BroadcastReceiver screenOnTimerReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i("[BroadcastReceiver]", "MyReceiver");
                    long prevTime = loadTime(Constants.SCREEN_ON_TIME_PREF);
                    if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                        screenOnStartTime = System.currentTimeMillis();
                        saveTime(screenOnStartTime, Constants.SCREEN_ON_START_TIME_PREF);
                        Log.i("[BroadcastReceiver]", "Screen ON " + screenOnStartTime);
                        Toast.makeText(context, "Total time " + convertMillisecondsToHMmSs(loadTime(Constants.SCREEN_ON_TIME_PREF)), Toast.LENGTH_SHORT).show();
                    }
                    else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                        screenOnEndTime = System.currentTimeMillis();
                        screenOnTime = screenOnEndTime - loadTime(Constants.SCREEN_ON_START_TIME_PREF);

                        if(screenOnTime < TIME_ERROR) {
                            saveTime(prevTime + screenOnTime, Constants.SCREEN_ON_TIME_PREF);

                            Log.i("[BroadcastReceiver]", "Screen OFF " + prevTime);
                            Log.i("[BroadcastReceiver]", "Total time " + convertMillisecondsToHMmSs(loadTime(Constants.SCREEN_ON_TIME_PREF)));
                        }

                    }

                }
            };
            context.registerReceiver(screenOnTimerReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
            context.registerReceiver(screenOnTimerReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

            return screenOnTimerReceiver;
        }

        public static void saveTime(Long time, String type){
            SharedPreferences settings;
            Editor editor;

            settings = context.getSharedPreferences(Constants.SCREEN_TIME_PREFS, Context.MODE_PRIVATE);
            editor = settings.edit();

            if(type.equals(Constants.SCREEN_ON_TIME_PREF)) {
                editor.putLong(Constants.SCREEN_ON_TIME_PREF, time);
            }
            else{
                editor.putLong(Constants.SCREEN_ON_START_TIME_PREF, time);
            }

            editor.commit();
        }

        public static long loadTime(String type){
            SharedPreferences settings;
            settings = context.getSharedPreferences(Constants.SCREEN_TIME_PREFS, Context.MODE_PRIVATE);
            if(type.equals(Constants.SCREEN_ON_TIME_PREF)) {
                return settings.getLong(Constants.SCREEN_ON_TIME_PREF, 0);
            }
            else{
                return settings.getLong(Constants.SCREEN_ON_START_TIME_PREF, 0);
            }
        }

        public static void clearTime(){
            SharedPreferences settings;
            Editor editor;

            settings = context.getSharedPreferences(Constants.SCREEN_TIME_PREFS, Context.MODE_PRIVATE);
            editor = settings.edit();
            editor.remove(Constants.SCREEN_ON_TIME_PREF);
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
