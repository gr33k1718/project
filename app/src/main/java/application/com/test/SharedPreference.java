package application.com.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SharedPreference {

    private static String PREFS_NAME = "LOCATIONS";
    private static String CHARGE_POINTS = "Charge_Points";

    private Calendar time = Calendar.getInstance();
    private Context context = GlobalVars.getAppContext();


    public SharedPreference(){
        super();
    }

    public void saveLocations(Context context, List<ChargeLocation> locations) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String chargeLocations = gson.toJson(locations);

        editor.putString(CHARGE_POINTS, chargeLocations);

        editor.commit();
    }

    public ArrayList<ChargeLocation> getChargeLocations(Context context) {
        SharedPreferences settings;
        List<ChargeLocation> locations;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (settings.contains(CHARGE_POINTS)) {
            String jsonLocations = settings.getString(CHARGE_POINTS, null);
            Gson gson = new Gson();
            ChargeLocation[] chargeLocations = gson.fromJson(jsonLocations,
                    ChargeLocation[].class);

            locations = Arrays.asList(chargeLocations);
            locations = new ArrayList<>(locations);
        } else
            return null;

        return (ArrayList<ChargeLocation>) locations;
    }

    public void removeLocation(int index){
        List<ChargeLocation> chargeLocations = getChargeLocations(this.context);
        chargeLocations.remove(index);

        saveLocations(context, chargeLocations);

    }

    public void addLocation(ChargeLocation location) {
        float currentDistance;
        float minDistance = 100;
        String date = DateFormat.getDateTimeInstance().format(new Date());
        List<ChargeLocation> chargeLocations = getChargeLocations(this.context);

        location.setLastChargeDate(date);

        if (chargeLocations == null) {
            chargeLocations = new ArrayList<>();
            chargeLocations.add(location);
        }
        else {
            if(!locationExists(location)){
                chargeLocations.add(location);
            }
            else {
                for (ChargeLocation l : chargeLocations) {
                    currentDistance = l.getLocation().distanceTo(location.getLocation());
                    if (currentDistance < minDistance) {
                        l.increaseVisits();
                        l.setChargingTime(time.get(Calendar.HOUR_OF_DAY));
                        l.setLastChargeDate(date);
                        //return;
                    }
                }
            }
        }

        saveLocations(context, chargeLocations);
    }


    public void clearLocations(Context context){
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(CHARGE_POINTS);
        editor.commit();
    }

    public String displayVisits(){
        String result = "";
        ArrayList<ChargeLocation> chargeLocations = getChargeLocations(this.context);

        for(ChargeLocation chargePoint : chargeLocations){
            result += chargePoint;
            result += "\nCharge times  " + Arrays.toString(chargePoint.getChargeTimes());
        }

        result += "\nNum locations " + chargeLocations.size();

        return result;
    }

    public boolean locationExists(ChargeLocation loc){
        for(ChargeLocation l : getChargeLocations(this.context)){
            if(l.getLocation().distanceTo(loc.getLocation()) < 100){
                return true;
            }
        }
        return false;
    }


    public long daysBetween(Long lastChargeDate, Long currentDate){
        long lastCharge = lastChargeDate / 1000 / 60 / 60 / 24;
        long current = currentDate / 1000 / 60 / 60 / 24;
        if(lastChargeDate== null || currentDate == null) {
            return -1;
        }
        else{
            return  lastCharge - current;
        }
    }
}
