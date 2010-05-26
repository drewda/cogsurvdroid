package org.cogsurv.cogsurver.content;

import java.io.IOException;
import java.util.ArrayList;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvCredentialsException;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvException;
import org.cogsurv.cogsurver.types.Landmark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Utility to access data from the CogSurver content provider.
 * 
 * @author Drew Dara-Abrams
 */
public class CogSurverProviderUtils {
  //Authority (first part of URI) for the CogSurver content provider:
  public static final String AUTHORITY = "org.cogsurv.cogsurver";

  private final Context context;
  
  private CogSurver mCogSurver;

  public CogSurverProviderUtils(Context context) {
    this.context = context;
  }

  public void loadLandmarks(int userId) {
    mCogSurver = new CogSurver(CogSurver.createHttpApi("v1", false));
    Log.d(CogSurverProvider.TAG, "CogSurverProviderUtils.readLandmarks");
    ArrayList<Landmark> landmarks = new ArrayList<Landmark>();
    
    if (connectedToInternet()) {
      try {
        landmarks = mCogSurver.readLandmarks();
      } catch (CogSurvCredentialsException e) {
        Log.d(CogSurverProvider.TAG, "CogSurverProviderUtils.readLandmarks CogSurvCredentialsException");
        e.printStackTrace();
      } catch (CogSurvError e) {
        Log.d(CogSurverProvider.TAG, "CogSurverProviderUtils.readLandmarks CogSurvError");
        e.printStackTrace();
      } catch (CogSurvException e) {
        Log.d(CogSurverProvider.TAG, "CogSurverProviderUtils.readLandmarks CogSurvException");
        e.printStackTrace();
      } catch (IOException e) {
        Log.d(CogSurverProvider.TAG, "CogSurverProviderUtils.readLandmarks IOException");
        e.printStackTrace();
      }
      for (Landmark landmark : landmarks) {
        context.getContentResolver().insert(LandmarksColumns.CONTENT_URI,
            createContentValues(landmark));
        Log.d(CogSurverProvider.TAG, "landmark: " + landmark.getName());
      }
    }
  }

  private static ContentValues createContentValues(Landmark landmark) {
    ContentValues values = new ContentValues();
    // Values id < 0 indicate no id is available:
    if (landmark.getLocalId() >= 0) {
      values.put(LandmarksColumns._ID, landmark.getLocalId());
    }
    values.put(LandmarksColumns.SERVER_ID, landmark.getServerId());
    values.put(LandmarksColumns.USER_ID, landmark.getUserId());
    values.put(LandmarksColumns.FOURSQUARE_VENUE_ID, landmark.getFoursquareVenueId());
    values.put(LandmarksColumns.NAME, landmark.getName());
    values.put(LandmarksColumns.ADDRESS, landmark.getAddress());
    values.put(LandmarksColumns.CITY, landmark.getCity());
    values.put(LandmarksColumns.STATE, landmark.getState());
    values.put(LandmarksColumns.ZIP, landmark.getZip());
    values.put(LandmarksColumns.LATITUDE, landmark.getLatitude());
    values.put(LandmarksColumns.LONGITUDE, landmark.getLongitude());
    return values;
  }
  
  public Landmark createLandmark(Cursor cursor) {
    int idxLocalId = cursor.getColumnIndexOrThrow(LandmarksColumns._ID);
    int idxServerId = cursor.getColumnIndexOrThrow(LandmarksColumns.SERVER_ID);
    int idxUserId = cursor.getColumnIndexOrThrow(LandmarksColumns.USER_ID);
    int idxFoursquareVenueId = cursor.getColumnIndexOrThrow(LandmarksColumns.FOURSQUARE_VENUE_ID);
    int idxName = cursor.getColumnIndexOrThrow(LandmarksColumns.NAME);
    int idxAddress = cursor.getColumnIndexOrThrow(LandmarksColumns.ADDRESS);
    int idxCity = cursor.getColumnIndexOrThrow(LandmarksColumns.CITY);
    int idxState = cursor.getColumnIndexOrThrow(LandmarksColumns.STATE);
    int idxZip = cursor.getColumnIndexOrThrow(LandmarksColumns.ZIP);
    int idxLatitude = cursor.getColumnIndexOrThrow(LandmarksColumns.LATITUDE);
    int idxLongitude = cursor.getColumnIndexOrThrow(LandmarksColumns.LONGITUDE);

    Landmark landmark = new Landmark();
    if (!cursor.isNull(idxLocalId)) {
      landmark.setLocalId(cursor.getInt(idxLocalId));
    }
    if (!cursor.isNull(idxServerId)) {
      landmark.setServerId(cursor.getInt(idxServerId));
    }
    if (!cursor.isNull(idxUserId)) {
      landmark.setUserId(cursor.getInt(idxUserId));
    }
    if (!cursor.isNull(idxFoursquareVenueId)) {
      landmark.setFoursquareVenueId(cursor.getString(idxFoursquareVenueId));
    }
    if (!cursor.isNull(idxName)) {
      landmark.setName(cursor.getString(idxName));
    }
    if (!cursor.isNull(idxAddress)) {
      landmark.setAddress(cursor.getString(idxAddress));
    }
    if (!cursor.isNull(idxCity)) {
      landmark.setCity(cursor.getString(idxCity));
    }
    if (!cursor.isNull(idxState)) {
      landmark.setState(cursor.getString(idxState));
    }
    if (!cursor.isNull(idxZip)) {
      landmark.setZip(cursor.getString(idxZip));
    }
    if (!cursor.isNull(idxLatitude)) {
      landmark.setLatitude(1. * cursor.getInt(idxLatitude) / 1E6);
    }
    if (!cursor.isNull(idxLongitude)) {
      landmark.setLongitude(1. * cursor.getInt(idxLongitude) / 1E6);
    }
    return landmark;
  }
  
  private boolean connectedToInternet() {
    ConnectivityManager connec = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    return connec.getNetworkInfo(0).isConnected();
  }
}
