package application.com.test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.*;

import java.util.Calendar;


public class PowerReceiver extends BroadcastReceiver implements ConnectionCallbacks, OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient apiClient;
    private Location location;
    private SharedPreference sharedPreference;
    private Calendar time;


    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,MyService.class);
        this.context = context;
        time = Calendar.getInstance();
        apiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Grab action from fired intent
        String action = intent.getAction();

        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
            //boolean wasStopped = context.stopService(i);
            Toast.makeText(context, "Power connected ", Toast.LENGTH_SHORT).show();
            sharedPreference = new SharedPreference();

            //Connect to google play services

            apiClient.connect();
        }
        else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)){
            Toast.makeText(context, "Power disconnected" , Toast.LENGTH_SHORT).show();

            //context.startService(i);
            //Disconnect from google play services
            apiClient.disconnect();

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        ChargeLocation chargeLocation;
        //sharedPreference.clearLocations(context);
        //Grab last location from WiFi or GSM. Only coarse location enabled
        location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        chargeLocation = new ChargeLocation(location, time.get(Calendar.HOUR_OF_DAY));

        sharedPreference.addLocation(chargeLocation);

        Toast.makeText(context, sharedPreference.displayVisits() +
                                " Hour of day " + time.get(Calendar.HOUR_OF_DAY),
                Toast.LENGTH_LONG).show();
        /*
        Location l = new Location("");
        l.setLatitude(51.891882);
        l.setLongitude(-8.495638);
        sharedPreference.addLocation(new ChargeLocation(l));
        */


    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(context, "Connection disconnected" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, "Connection failed" , Toast.LENGTH_SHORT).show();
    }
}
