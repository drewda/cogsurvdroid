package org.cogsurv.droid;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.content.CogSurverProvider;
import org.cogsurv.cogsurver.content.DirectionDistanceEstimatesColumns;
import org.cogsurv.cogsurver.content.LandmarkVisitsColumns;
import org.cogsurv.cogsurver.content.LandmarksColumns;
import org.cogsurv.cogsurver.content.TravelFixesColumns;
import org.cogsurv.cogsurver.error.CogSurvCredentialsException;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvException;
import org.cogsurv.cogsurver.types.DirectionDistanceEstimate;
import org.cogsurv.cogsurver.types.Group;
import org.cogsurv.cogsurver.types.Landmark;
import org.cogsurv.cogsurver.types.LandmarkVisit;
import org.cogsurv.cogsurver.types.TravelFix;
import org.cogsurv.cogsurver.types.User;
import org.cogsurv.droid.app.TravelLogService;
import org.cogsurv.droid.preferences.Preferences;
import org.cogsurv.droid.util.JavaLoggingHandler;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class CogSurvDroid extends Application {
    public static final String TAG = "CogSurvDroid";
    private static final boolean DEBUG = CogSurvDroidSettings.DEBUG;
    static {
        Logger.getLogger("org.cogsurv.droid").addHandler(new JavaLoggingHandler());
        Logger.getLogger("org.cogsurv.droid").setLevel(Level.ALL);
    }

    public static final String PACKAGE_NAME = "org.cogsurv.droid";

    public static final String INTENT_ACTION_LOGGED_OUT = "org.cogsurv.droid.intent.action.LOGGED_OUT";
    public static final String INTENT_ACTION_LOGGED_IN = "org.cogsurv.droid.intent.action.LOGGED_IN";

    private String mVersion = null;

    private TaskHandler mTaskHandler;
    private HandlerThread mTaskThread;

    private SharedPreferences mPrefs;

    private CogSurver mCogSurver;
    
    public Group<Landmark> mEstimatesTargetSet;

    @Override
    public void onCreate() {
        Log.i(TAG, "Using Debug Log:\t" + DEBUG);

        mVersion = getVersionString(this);

        // Setup Prefs (to load dumpcatcher)
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Sometimes we want the application to do some work on behalf of the
        // Activity. Lets do that
        // asynchronously.
        mTaskThread = new HandlerThread(TAG + "-AsyncThread");
        mTaskThread.start();
        mTaskHandler = new TaskHandler(mTaskThread.getLooper());

        // Catch logins or logouts.
        new LoggedInOutBroadcastReceiver().register();

        // Log into CogSurv, if we can.
        loadCogSurver();
    }

    public boolean isReady() {
        return getCogSurver().hasLoginAndPassword() && !TextUtils.isEmpty(getUserId());
    }

    public CogSurver getCogSurver() {
        return mCogSurver;
    }
    
    public String getUserId() {
        return Preferences.getUserId(mPrefs);
    }

    public String getVersion() {

        if (mVersion != null) {
            return mVersion;
        } else {
            return "";
        }
    }

    public void requestStartService() {
        mTaskHandler.sendMessage( //
                mTaskHandler.obtainMessage(TaskHandler.MESSAGE_START_SERVICE));
    }

    public void requestUpdateUser() {
        mTaskHandler.sendEmptyMessage(TaskHandler.MESSAGE_UPDATE_USER);
    }

    private void loadCogSurver() {
        // Try logging in and setting up foursquare oauth, then user
        // credentials.
        mCogSurver = new CogSurver(CogSurver.createHttpApi(mVersion));

        if (CogSurvDroidSettings.DEBUG) Log.d(TAG, "loadCredentials()");
        String email = mPrefs.getString(Preferences.PREFERENCE_LOGIN, null);
        String password = mPrefs.getString(Preferences.PREFERENCE_PASSWORD, null);
        mCogSurver.setCredentials(email, password);
        if (mCogSurver.hasLoginAndPassword()) {
            sendBroadcast(new Intent(INTENT_ACTION_LOGGED_IN));
        } else {
            sendBroadcast(new Intent(INTENT_ACTION_LOGGED_OUT));
        }
    }

    /**
     * Provides static access to a Foursquare instance. This instance is
     * initiated without user credentials.
     * 
     * @param context the context to use when constructing the Foursquare
     *            instance
     * @return the Foursquare instace
     */
    public static CogSurver createCogSurver(Context context) {
        String version = getVersionString(context);
        return new CogSurver(CogSurver.createHttpApi(version));
    }

    /**
     * Constructs the version string of the application.
     * 
     * @param context the context to use for getting package info
     * @return the versions string of the application
     */
    private static String getVersionString(Context context) {
        // Get a version string for the app.
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(PACKAGE_NAME, 0);
            return PACKAGE_NAME + ":" + String.valueOf(pi.versionCode);
        } catch (NameNotFoundException e) {
            if (DEBUG) Log.d(TAG, "Could not retrieve package info", e);
            throw new RuntimeException(e);
        }
    }

    private class LoggedInOutBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (INTENT_ACTION_LOGGED_IN.equals(intent.getAction())) {
                requestUpdateUser();
            }
        }

        public void register() {
            // Register our media card broadcast receiver so we can
            // enable/disable the cache as
            // appropriate.
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(INTENT_ACTION_LOGGED_IN);
            intentFilter.addAction(INTENT_ACTION_LOGGED_OUT);
            registerReceiver(this, intentFilter);
        }

    }

    private class TaskHandler extends Handler {

        private static final int MESSAGE_UPDATE_USER = 1;
        private static final int MESSAGE_START_SERVICE = 2;

        public TaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (DEBUG) Log.d(TAG, "handleMessage: " + msg.what);

            switch (msg.what) {
                case MESSAGE_UPDATE_USER:
                    try {
                        // Update user info
                        Log.d(TAG, "Updating user.");
                        User user = getCogSurver().readUser();
                        Editor editor = mPrefs.edit();
                        Preferences.storeUser(editor, user);
                        editor.commit();

                    } catch (CogSurvError e) {
                        if (DEBUG) Log.d(TAG, "CogSurvError", e);
                    } catch (CogSurvException e) {
                        if (DEBUG) Log.d(TAG, "CogSurvException", e);
                    } catch (IOException e) {
                        if (DEBUG) Log.d(TAG, "IOException", e);
                    }
                    return;

                case MESSAGE_START_SERVICE:
                    Intent serviceIntent = new Intent(CogSurvDroid.this, TravelLogService.class);
                    startService(serviceIntent);
                    return;
            }
        }
    }
    
    /* DATA HANDLERS */
    /* TRAVEL FIX */
    public boolean recordTravelFix(TravelFix travelFix) {
      Log.d("CogSurv", "CogSurvDroid.recordTravelFix");
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
      travelFix.setUserId(Integer.valueOf(getUserId()));
      Uri uri = getContentResolver().insert(TravelFixesColumns.CONTENT_URI,
          CogSurverProvider.createContentValues(travelFix));
      long localId = Long.parseLong(uri.getLastPathSegment());
      Log.d("CogSurv", "recordTravelFix: insert into ContentProvider: localId = " + localId);

      return true;
    }
    
    /* LANDMARK */
    public Cursor readLandmarks(boolean hitServer) {
      Log.v("CogSurv", "CogSurvDroid.readLandmarks(hitServer=" + hitServer + ")");
      
      if (hitServer) {
        Log.v("CogSurv", "CogSurvDroid.readLandmarks hitting server");
        Group<Landmark> landmarks = new Group<Landmark>();
        
        try {
          landmarks = mCogSurver.readLandmarks();
        } catch (CogSurvCredentialsException e) {
          e.printStackTrace();
        } catch (CogSurvError e) {
          e.printStackTrace();
        } catch (CogSurvException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        
        if (landmarks.size() > 0) {
          for (Landmark landmark : landmarks) { 
            int rowsDeleted = getContentResolver().delete(LandmarksColumns.CONTENT_URI, LandmarksColumns.SERVER_ID + "=" + landmark.getServerId(), null);
            Log.v("CogSurv", "CogSurvDroid.readLandmarks(hitServer=true) rows deleted: " + rowsDeleted);
            Uri uri = getContentResolver().insert(LandmarksColumns.CONTENT_URI, CogSurverProvider.createContentValues(landmark));
            Log.v("CogSurv", "CogSurvDroid.readLandmarks(hitServer=true) row inserted: " + uri.toString());
          }
        }
      }
      
      return getContentResolver().query(LandmarksColumns.CONTENT_URI, null, LandmarksColumns.USER_ID + "=" + getUserId(), null, null);
    }
    
    /* LANDMARK VISIT */
    public LandmarkVisit recordLandmarkVisit(LandmarkVisit landmarkVisit) {
      Log.d("CogSurv", "CogSurvDroid.recordLandmarkVisit");
      // if connected to Internet, post to server
      try {
        landmarkVisit = mCogSurver.createLandmarkVisit(landmarkVisit);
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
      landmarkVisit.setUserId(Integer.valueOf(getUserId()));
      Uri uri = getContentResolver().insert(LandmarkVisitsColumns.CONTENT_URI,
          CogSurverProvider.createContentValues(landmarkVisit));
      long localId = Long.parseLong(uri.getLastPathSegment());
      Log.d("CogSurv", "recordLandmarkVisit: insert into ContentProvider: localId = " + localId);
      landmarkVisit.setLocalId((int)localId);
      
      return landmarkVisit;
    }
    
    /* DIRECTION DISTANCE ESTIMATE */
    public DirectionDistanceEstimate recordDirectionDistanceEstimate(DirectionDistanceEstimate directionDistanceEstimate) {
      Log.d("CogSurv", "CogSurvDroid.recordDirectionDistanceEstimate");
      // if connected to Internet, post to server
      try {
        directionDistanceEstimate = mCogSurver.createDirectionDistanceEstimate(directionDistanceEstimate);
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
      directionDistanceEstimate.setUserId(Integer.valueOf(getUserId()));
      Uri uri = getContentResolver().insert(DirectionDistanceEstimatesColumns.CONTENT_URI,
          CogSurverProvider.createContentValues(directionDistanceEstimate));
      long localId = Long.parseLong(uri.getLastPathSegment());
      Log.d("CogSurv", "recordDirectionDistanceEstimate: insert into ContentProvider: localId = " + localId);
      directionDistanceEstimate.setLocalId((int)localId);

      return directionDistanceEstimate;
    }
}
