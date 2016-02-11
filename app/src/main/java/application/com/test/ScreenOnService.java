package application.com.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class ScreenOnService extends Service {

    BroadcastReceiver screenOnTimer;
    Context context = GlobalVars.getAppContext();
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Created ", Toast.LENGTH_SHORT).show();
        screenOnTimer = DisplayContext.InteractionTimer.setupTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Started ", Toast.LENGTH_SHORT).show();


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroyed ", Toast.LENGTH_SHORT).show();
        context.unregisterReceiver(screenOnTimer);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
