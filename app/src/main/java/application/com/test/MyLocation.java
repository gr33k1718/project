package application.com.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.*;


public class MyLocation implements ConnectionCallbacks, OnConnectionFailedListener{
    private GoogleApiClient apiClient;
    private Context context = GlobalVars.getAppContext();
    Location location;
    double longitude;
    double latitude;

    public MyLocation(){
        apiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        apiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        Toast.makeText(context,"Hi " + longitude,Toast.LENGTH_SHORT).show();
        apiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
