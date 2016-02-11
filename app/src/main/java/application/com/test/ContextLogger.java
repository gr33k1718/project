package application.com.test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class ContextLogger {
    private static final String DATABASE_NAME = "logs.db";
    private static final int DATABASE_VERSION = 4;
    private static final String LOG_TABLE_NAME = "logs";

    private static final String KEY_ID = "_id";
    private  static final String PERIOD = "timePeriod";

    private final SQLOpenHelper mSQLOpenHelper;
    private SQLiteDatabase rdb;
    private SQLiteDatabase wdb;

    public ContextLogger(Context context) {
        mSQLOpenHelper = new SQLOpenHelper(context);

        openDBs();
    }

    private void openDBs(){
        if (rdb == null || !rdb.isOpen()) {
            try {
                rdb = mSQLOpenHelper.getReadableDatabase();
            } catch (SQLiteException e) {
                rdb = null;
            }
        }

        if (wdb == null || !wdb.isOpen()) {
            try {
                wdb = mSQLOpenHelper.getWritableDatabase();
            } catch (SQLiteException e) {
                rdb = null;
            }
        }
    }

    public void close() {
        if (rdb != null)
            rdb.close();
        if (wdb != null)
            wdb.close();
    }

    public Cursor getAllLogs() {

        openDBs();

        try {
            return rdb.rawQuery("SELECT * FROM " + LOG_TABLE_NAME,null);
        } catch (Exception e) {
            return null;
        }
    }

    public void logStatus(SystemContext info) {

        openDBs();

        try {
                wdb.execSQL("INSERT INTO " + LOG_TABLE_NAME + " VALUES (NULL, "
                        + info.period + " ,"
                        + info.longitude + " ,"
                        + info.latitude + " ,"
                        + info.brightness + " ,"
                        + info.timeOut + " ,"
                        + info.interactionTime + " ,"
                        + info.batteryLevel + " ,"
                        + info.networkTraffic + " ,"
                        + info.mobileTraffic
                        + ")");
            Log.d("[Database]", "Success " + info.period);
        } catch (Exception e) {
            Log.d("[Database] " + info.period, e.toString());
        }
    }
      /*
    public void prune(int max_hours) {
        long currentTM = System.currentTimeMillis();
        long oldest_log = currentTM - ((long) max_hours * 60 * 60 * 1000);

        openDBs();

        try {
            wdb.execSQL("DELETE FROM " + LOG_TABLE_NAME + " WHERE " + KEY_TIME + " < " + oldest_log);
        } catch (Exception e) {
            // Maybe storage is full?  Okay to just return rather than crash.
        }
    }

    /* My cursor adapter was getting a bit complicated since it could only see one datum at a time, and
       how I want to present the data depends on several interrelated factors.  Storing all three of
       these items together simplifies things. */
    private static int encodeStatus(int status, int plugged, int status_age) {
        return status + (plugged * 10) + (status_age * 100);
    }

    /* Returns [status, plugged, status_age] */
    public static int[] decodeStatus(int statusCode) {
        int[] a = new int[3];

        a[2] = statusCode / 100;
        statusCode -= a[2] * 100;
        a[1] = statusCode / 10;
        statusCode -= a[1] * 10;
        a[0] = statusCode;

        return a;
    }

    public void clearAllLogs() {
        mSQLOpenHelper.reset();
    }

    private static class SQLOpenHelper extends SQLiteOpenHelper {
        public SQLOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + LOG_TABLE_NAME + " ("
                    + KEY_ID            + " INTEGER PRIMARY KEY,"
                    + Constants.PERIOD_PREF            + " INTEGER,"
                    + Constants.LOCATION_LONG_PREF     + " REAL,"
                    + Constants.LOCATION_LAT_PREF      + " REAL,"
                    + Constants.BRIGHTNESS_PREF        + " INTEGER,"
                    + Constants.TIMEOUT_PREF           + " INTEGER,"
                    + Constants.INTERACTION_TIME_PREF  + " INTEGER,"
                    + Constants.BATTERY_LEVEL_PREF     + " INTEGER,"
                    + Constants.NETWORK_TRAFFIC_PREF   + " INTEGER,"
                    + Constants.MOBILE_TRAFFIC_PREF    + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
                onCreate(db);

        }

        public void reset() {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
            onCreate(db);
        }
    }
}
