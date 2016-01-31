package application.com.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Handler;

public class MainActivity extends Activity {

    private TextView tv;
    private ListView lv;
    private SharedPreference prefs;
    private Button mUpdateButton;
    private ArrayList<ChargeLocation> chargeLocations;
    private AdapterChargeLocation myLoc;
    private String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String b = Settings.System.getString(this.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
        String a = Settings.Global.getString(this.getContentResolver(), Settings.Global.WIFI_SLEEP_POLICY);
        tv = (TextView)findViewById(R.id.textView);
        tv.setText(b);
        setup();

        setupListeners();

        Toast.makeText(this,meminfo(),Toast.LENGTH_LONG).show();
    }

    public void setup(){
        prefs = new SharedPreference();
        chargeLocations = prefs.getChargeLocations(this);
        myLoc = new AdapterChargeLocation(this, R.layout.charge_location_list_item, chargeLocations);

    }

    public void setupListeners(){
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
        mUpdateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<ChargeLocation> chargeLocations = prefs.getChargeLocations(getApplicationContext());
                myLoc.clear();
                myLoc.addAll(chargeLocations);
                myLoc.notifyDataSetChanged();

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

    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public String meminfo(){
        final Runtime runtime = Runtime.getRuntime();

                try {
                    String s = null;
                    Process a = runtime.exec("adb shell dumpsys meminfo");
                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(a.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new
                            InputStreamReader(a.getErrorStream()));


                    // read the output from the command
                    System.out.println("Here is the standard output of the command:\n");
                    while ((s = stdInput.readLine()) != null) {
                        Log.d("bufferedReader", s);
                    }



                    // read any errors from the attempted command
                    System.out.println("Here is the standard error of the command (if any):\n");
                    while ((s = stdError.readLine()) != null) {
                        Log.d("bufferedReader", s);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        return s;


    }
}
