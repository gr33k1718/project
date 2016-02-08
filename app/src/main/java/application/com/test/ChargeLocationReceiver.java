package application.com.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.*;

import java.util.Calendar;


public class ChargeLocationReceiver extends BroadcastReceiver implements ConnectionCallbacks, OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient apiClient;
    private Location location;
    private SharedPreference sharedPreference = new SharedPreference();;
    private Calendar time;


    public void onReceive(Context context, Intent intent) {
        this.context = context;
        time = Calendar.getInstance();
        apiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        String action = intent.getAction();

        switch(action){
            case Intent.ACTION_POWER_CONNECTED:
                Toast.makeText(context, "Power connected ", Toast.LENGTH_SHORT).show();
                apiClient.connect();
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                Toast.makeText(context, "Power disconnected" , Toast.LENGTH_SHORT).show();
                apiClient.disconnect();
                break;
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_PACKAGE_REPLACED:
                context.startService(new Intent(context, ScreenOnService.class));
                break;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        ChargeLocation chargeLocation;

        location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        chargeLocation = new ChargeLocation(location, time.get(Calendar.HOUR_OF_DAY));

        sharedPreference.addLocation(chargeLocation);

        Toast.makeText(context, sharedPreference.displayVisits() +
                                " Hour of day " + time.get(Calendar.HOUR_OF_DAY),
                Toast.LENGTH_LONG).show();

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
