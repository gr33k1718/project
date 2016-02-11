package application.com.test;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {

    private TextView tv;
    private TextView tv2;
    private ListView lv;
    private SharedPreference prefs;
    private Button mUpdateButton;
    private Button mUsageButton;
    private ArrayList<ChargeLocation> chargeLocations;
    private AdapterChargeLocation myLoc;

    private final String RX_FILE = "/sys/class/net/wlan0/statistics/rx_bytes";
    private final String TX_FILE = "/sys/class/net/wlan0/statistics/tx_bytes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String b = Settings.System.getString(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        String c = Formatter.formatFileSize(this, NetworkContext.getNetworkTraffic());
        String a = Formatter.formatFileSize(this, NetworkContext.getMobileTraffic());

        tv2 = (TextView)findViewById(R.id.textView2);
        tv = (TextView)findViewById(R.id.textView);
        tv.setText(String.valueOf(b));
        tv2.setText(a);
        setup();
        scheduleAlarm();


    }

    public void setup(){
        prefs = new SharedPreference();
        chargeLocations = prefs.getChargeLocations(this);
        myLoc = new AdapterChargeLocation(this, R.layout.charge_location_list_item, chargeLocations);
        updateAdapter();
        removeAdapterItem();
        lv.setAdapter(myLoc);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void updateAdapter(){
        mUpdateButton = (Button)findViewById(R.id.updateButton);
        mUsageButton = (Button)findViewById(R.id.usageButton);

        mUpdateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<ChargeLocation> chargeLocations = prefs.getChargeLocations(getApplicationContext());
                myLoc.clear();
                myLoc.addAll(chargeLocations);
                myLoc.notifyDataSetChanged();

            }
        });

        mUsageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
        });

    }

    public void removeAdapterItem(){
        lv = (ListView) findViewById(R.id.mylist);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        MainActivity.this);
                alert.setTitle("Caution!!");
                alert.setMessage("Once deleted gone forever");
                alert.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefs.removeLocation(position);
                        chargeLocations.remove(position);
                        myLoc.notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
    }

    public void scheduleAlarm()
    {
        //Create alarm at the top of the hour
        Calendar time = Calendar.getInstance();
        Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show();
        time.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY) +1);
        time.set(Calendar.MINUTE, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getService(this, 1, new Intent(this, LogService.class), PendingIntent.FLAG_UPDATE_CURRENT));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), AlarmManager.INTERVAL_HOUR,
                PendingIntent.getService(this, 1, new Intent(this, LogService.class), PendingIntent.FLAG_UPDATE_CURRENT));

    }
}
