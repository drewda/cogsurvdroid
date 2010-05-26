/**
 * 
 */
package org.cogsurv.droid.app;

import java.util.Timer;
import java.util.TimerTask;

import org.cogsurv.droid.CogSurvDroidSettings;

import edu.ucsb.cogsurv.Constants;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

/**
 * @author Drew Dara-Abrams
 * 
 */
public class TravelLogService extends Service {
  private Timer                 timer       = new Timer();

  private PowerManager          pm;
  private PowerManager.WakeLock wl;

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

  /**
     * 
     */
  @Override
  public void onCreate() {
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, CogSurvDroidSettings.TRAVEL_LOG_INTERVAL, 0, gpsListener);
  }
  
  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
    
    timer.scheduleAtFixedRate(
        new TimerTask() {
          public void run() {
            processGPS();
          }
        },
        0,
        Constants.LOG_INTERVAL);
    
    settings = getSharedPreferences(Constants.PREFERENCES, 0);
    current_person_id = settings.getInt(Constants.CURRENT_PERSON_SERVER_ID_PREF, -1);
    current_travel_log_id = settings.getInt(Constants.CURRENT_TRAVEL_LOG_SERVER_ID_PREF, -1);
    
    pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TravelLogServiceLock");
    wl.acquire();
  }
  
  @Override
  public void onDestroy() {
    timer.cancel();
    locationManager.removeUpdates(gpsListener);
    wl.release();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}
