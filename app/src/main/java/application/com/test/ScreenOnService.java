package application.com.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;

public class ScreenOnService extends Service {

    BroadcastReceiver screenOnTimer;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        screenOnTimer = DisplayContext.InteractionTimer.setupTimer();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenOnTimer);
        startService(new Intent(this, ScreenOnService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
