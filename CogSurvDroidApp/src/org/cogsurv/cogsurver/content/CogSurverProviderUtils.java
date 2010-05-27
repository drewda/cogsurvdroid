package org.cogsurv.cogsurver.content;

import java.io.IOException;
import java.util.ArrayList;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvCredentialsException;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvException;
import org.cogsurv.cogsurver.types.Landmark;
import org.cogsurv.cogsurver.types.TravelFix;
import org.cogsurv.droid.CogSurvDroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
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

  /* LANDMARK */
  /*
  public void loadLandmarks(int userId) {
    mCogSurver = new CogSurver(CogSurver.createHttpApi("v1"));
    mCogSurver = ((CogSurvDroid) this.context.getApplicationContext()..getApplication()).getCogSurver();
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


  

  
  /* TRAVEL FIX 
  public boolean recordTravelFix(TravelFix travelFix) {
    Log.d("CogSurv", "CogSurverProviderUtils.recordTravelFix");
    // if connected to Internet, post to server
    try {
      travelFix = mCogSurver.createTravelFix(travelFix);
    } catch (CogSurvCredentialsException e) {
      e.printStackTrace();
    } catch (CogSurvError e) {
      e.printStackTrace();
    } catch (CogSurvException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    // in any case, record to DB
    Uri uri = context.getContentResolver().insert(TravelFixesColumns.CONTENT_URI,
        createContentValues(travelFix));
    long localId = Long.parseLong(uri.getLastPathSegment());
    Log.d("CogSurv", "recordTravelFix: insert into ContentProvider: localId = " + localId);

    return true;
  }
  

  
  public boolean connectedToInternet() {
    ConnectivityManager connec = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    return connec.getNetworkInfo(0).isConnected();
  }*/
}
