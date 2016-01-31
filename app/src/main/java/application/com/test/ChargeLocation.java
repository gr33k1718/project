package application.com.test;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by gr33k1718 on 19/01/2016.
 */
public class ChargeLocation {
    private String lastChargeTDate;
    private Location location;
    private int numOfVisits;
    private int[] chargingTime;

    public ChargeLocation(Location location, int time){
        this.location = location;
        numOfVisits = 1;
        chargingTime = new int[24];
        lastChargeTDate = null;
        setChargingTime(time);
    }

    public void setLastChargeDate(String date){lastChargeTDate = date;}

    public String getLastChargeDate(){return lastChargeTDate;}

    public Location getLocation() {return location;}

    public int[] getChargeTimes(){return chargingTime;}

    public void setChargingTime(int hour){chargingTime[hour] = chargingTime[hour] + 1;}

    public void increaseVisits(){numOfVisits = numOfVisits + 1;}

    public String displayChargingHoursFreq(){return Arrays.toString(chargingTime);}

    public String frequency(){
        String output = "";
        int hour = 0;
        double freq;

        for(int j : chargingTime){
            if(j > 0){
                freq = ((double)j/numOfVisits) * 100;
                String timeOfDay = (hour/12) == 0 ? hour + "am" : hour + "pm";

                output += "Hour " + timeOfDay +" has frequency " + (int)freq + "%\n";
            }
            hour++;
        }

        return output;
    }

    public String getAddress() {
        String result = null;
        List<Address> list;
        Geocoder geocoder = new Geocoder(GlobalVars.getAppContext(), Locale.getDefault());

        try {
            list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                result = address.getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public String toString(){

        return "Address " + getAddress() +
                "\nCharge point visits " + numOfVisits +
                "\nCharging hour frequency " + displayChargingHoursFreq()+
                "\nLast charge date " + getLastChargeDate() +
                "\n" + frequency();
    }


}
