package org.cogsurv.droid;

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
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvException;
import org.cogsurv.cogsurver.types.User;
import org.cogsurv.droid.app.TravelLogService;
import org.cogsurv.droid.error.LocationException;
import org.cogsurv.droid.location.BestLocationListener;
import org.cogsurv.droid.preferences.Preferences;
import org.cogsurv.droid.util.JavaLoggingHandler;

public class CogSurvDroid extends Application {
    private static final String TAG = "CogSurvDroid";
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

    private BestLocationListener mBestLocationListener = new BestLocationListener();

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

        // Log into Foursquare, if we can.
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

    public BestLocationListener requestLocationUpdates(boolean gps) {
        mBestLocationListener.register(
                (LocationManager) getSystemService(Context.LOCATION_SERVICE), gps);
        return mBestLocationListener;
    }

    public BestLocationListener requestLocationUpdates(Observer observer) {
        mBestLocationListener.addObserver(observer);
        mBestLocationListener.register(
                (LocationManager) getSystemService(Context.LOCATION_SERVICE), true);
        return mBestLocationListener;
    }

    public void removeLocationUpdates() {
        mBestLocationListener
                .unregister((LocationManager) getSystemService(Context.LOCATION_SERVICE));
    }

    public void removeLocationUpdates(Observer observer) {
        mBestLocationListener.deleteObserver(observer);
        this.removeLocationUpdates();
    }

    public Location getLastKnownLocation() {
        return mBestLocationListener.getLastKnownLocation();
    }

    public Location getLastKnownLocationOrThrow() throws LocationException {
        Location location = mBestLocationListener.getLastKnownLocation();
        if (location == null) {
            throw new LocationException();
        }
        return location;
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
        mCogSurver = new CogSurver(CogSurver.createHttpApi(mVersion, false));

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
        return new CogSurver(CogSurver.createHttpApi(version, false));
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
}