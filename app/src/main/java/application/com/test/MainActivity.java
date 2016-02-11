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
        String c = Formatter.formatFileSize(this, readFile(TX_FILE));
        String a = Formatter.formatFileSize(this, TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes());
        MyLocation my = new MyLocation();
        double longi = my.getLongitude();
        /*
        int d = getCpuUsageStatistic()[1];
        int f = getCpuUsageStatistic()[2];
        int g = getCpuUsageStatistic()[3];
        String shit = a + " " + d + " " + f + " " + g;
        */
        tv2 = (TextView)findViewById(R.id.textView2);
        tv = (TextView)findViewById(R.id.textView);
        tv.setText(String.valueOf(longi));
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
        alarmManager.cancel(PendingIntent.getService(this, 1, new Intent(this, BatteryService.class), PendingIntent.FLAG_UPDATE_CURRENT));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), AlarmManager.INTERVAL_HOUR,
                PendingIntent.getService(this, 1, new Intent(this, BatteryService.class), PendingIntent.FLAG_UPDATE_CURRENT));

    }

    public String getCPUUsage(int pid) {
        Process p;
        String line = "";
        try {
            String[] cmd = {
                    "sh",
                    "-c",
                    "top -m 1000 -d 1 -n 1"}; // | grep \"" + pid + "\" "};
            p = Runtime.getRuntime().exec(cmd);
            line = p.toString();
            // line contains the process info
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
    private int[] getCpuUsageStatistic() {

        String tempString = executeTop();

        tempString = tempString.replaceAll(",", "");
        tempString = tempString.replaceAll("User", "");
        tempString = tempString.replaceAll("System", "");
        tempString = tempString.replaceAll("IOW", "");
        tempString = tempString.replaceAll("IRQ", "");
        tempString = tempString.replaceAll("%", "");
        for (int i = 0; i < 10; i++) {
            tempString = tempString.replaceAll("  ", " ");
        }
        tempString = tempString.trim();
        String[] myString = tempString.split(" ");
        int[] cpuUsageAsInt = new int[myString.length];
        for (int i = 0; i < myString.length; i++) {
            myString[i] = myString[i].trim();
            cpuUsageAsInt[i] = Integer.parseInt(myString[i]);
        }
        return cpuUsageAsInt;
    }

    private String executeTop() {
        java.lang.Process p = null;
        BufferedReader in = null;
        String returnString = null;
        try {
            p = Runtime.getRuntime().exec("top -m 1 -n 1");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (returnString == null || returnString.contentEquals("")) {
                returnString = in.readLine();
            }
        } catch (IOException e) {
            Log.e("executeTop", "error in getting first line of top");
            e.printStackTrace();
        } finally {
            try {
                in.close();
                p.destroy();
            } catch (IOException e) {
                Log.e("executeTop",
                        "error in closing and destroying top process");
                e.printStackTrace();
            }
        }
        return returnString;
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
