/**
 * 
 */
package org.cogsurv.droid.app;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.cogsurv.cogsurver.types.TravelFix;
import org.cogsurv.droid.CogSurvDroid;
import org.cogsurv.droid.preferences.Preferences;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * @author Drew Dara-Abrams
 * 
 */
public class TravelLogService extends Service { 
  private static String         TAG = "TravelLogService";
  private int                   travel_log_interval; // milliseconds
  private Timer                 timer       = new Timer();

  private PowerManager          pm;
  private WakeLock              wl;

  private LocationManager       locationManager;
  private Location              gpsFix;
  private LocationListener      gpsListener = new LocationListener() {
    public void onLocationChanged(Location loc) {
      gpsFix = new Location(loc);
    }

    public void onProviderDisabled(String arg0) {
    }

    public void onProviderEnabled(String arg0) {
    }

    public void onStatusChanged(String arg0, int arg1,
        Bundle arg2) {
    }
  };
  private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        stopSelf();
    }
};

  /**
     * 
     */
  @Override
  public void onCreate() {
    registerReceiver(mLoggedOutReceiver, new IntentFilter(CogSurvDroid.INTENT_ACTION_LOGGED_OUT));
    
    pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TravelLogServiceLock");
    wl.acquire();
  }
  
  @Override
  public void onStart(Intent intent, int startId) {
    Log.d(TAG, "TravelLogService.onStart");
    super.onStart(intent, startId);
    
    Bundle extras = intent.getExtras();
    travel_log_interval = Integer.parseInt(extras.getString(Preferences.PREFERENCE_TRAVEL_LOG_INTERVAL));
    
    Log.d(TAG, "logging at interval: " + travel_log_interval + " milliseconds");
    
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, travel_log_interval, 0, gpsListener);
    
    timer.scheduleAtFixedRate(
        new TimerTask() {
          public void run() {
            if (gpsFix != null) {
              processGPS();
            }
          }
        },
        0,
        travel_log_interval);
    
    //pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    //wl.acquire();
  }
  
  @Override
  public void onDestroy() {
    timer.cancel();
    locationManager.removeUpdates(gpsListener);
    wl.release();
    unregisterReceiver(mLoggedOutReceiver);
    super.onDestroy();
    Log.d(TAG, "TravelLogService.onDestroy");
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  
  private void processGPS() {
    Log.d("CogSurv", "TravelLogService.processGPS");
    TravelFix travelFix = new TravelFix();
    travelFix.setDatetime(new Date());
    travelFix.setLatitude(gpsFix.getLatitude());
    travelFix.setLongitude(gpsFix.getLongitude());
    travelFix.setAccuracy(gpsFix.getAccuracy());
    travelFix.setAltitude(gpsFix.getAltitude());
    travelFix.setSpeed(gpsFix.getSpeed());
    
    ((CogSurvDroid) getApplication()).recordTravelFix(travelFix);
  }

}
