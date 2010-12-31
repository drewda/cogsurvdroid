/**
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.droid.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.io.IOException;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvCredentialsException;
import org.cogsurv.cogsurver.error.CogSurvException;
import org.cogsurv.cogsurver.types.User;
import org.cogsurv.droid.CogSurvDroidSettings;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public class Preferences {

    private static final String TAG = "Preferences";
    private static final boolean DEBUG = CogSurvDroidSettings.DEBUG;

    // Visible Preferences (sync with preferences.xml)
    public static final String PREFERENCE_SHARE_CHECKIN = "share_checkin";
    public static final String PREFERENCE_IMMEDIATE_CHECKIN = "immediate_checkin";

    // Hacks for preference activity extra UI elements.
    public static final String PREFERENCE_LOGOUT = "logout";

    // Credentials related preferences
    public static final String PREFERENCE_LOGIN = "login";
    public static final String PREFERENCE_PASSWORD = "password";

    // Extra info for getUserId
    private static final String PREFERENCE_ID = "id";
    
    // Last used distance unit
    private static final String PREFERENCE_LAST_USED_DISTANCE_UNIT = "last_used_distance_unit";
    
    public static final String PREFERENCE_TRAVEL_LOG_ENABLED = "travel_log_enabled";
    public static final String PREFERENCE_TRAVEL_LOG_INTERVAL = "travel_log_interval";

  public static boolean loginUser(CogSurver cogSurver, String login,
      String password, Editor editor) throws CogSurvCredentialsException,
      CogSurvException, IOException {
        if (DEBUG) Log.d(Preferences.TAG, "Trying to log in.");

        cogSurver.setCredentials(login, password);
        storeLoginAndPassword(editor, login, password);
        if (!editor.commit()) {
            if (DEBUG) Log.d(TAG, "storeLoginAndPassword commit failed");
            return false;
        }

        User user = cogSurver.readUser();
        storeUser(editor, user);
        if (!editor.commit()) {
            if (DEBUG) Log.d(TAG, "storeUser commit failed");
            return false;
        }

        return true;
    }

    public static boolean logoutUser(CogSurver cogSurver, Editor editor) {
        if (DEBUG) Log.d(Preferences.TAG, "Trying to log out.");
        // TODO: If we re-implement oAuth, we'll have to call clearAllCrendentials here.
        cogSurver.clearAllCredentials();
        return editor.clear().commit();
    }

    public static String getUserId(SharedPreferences prefs) {
        return prefs.getString(PREFERENCE_ID, null);
    }

    public static void storeLoginAndPassword(final Editor editor, String login, String password) {
        editor.putString(PREFERENCE_LOGIN, login);
        editor.putString(PREFERENCE_PASSWORD, password);
    }

    public static void storeUser(final Editor editor, User user) {
        if (user != null && user.getId() != null) {
            editor.putString(PREFERENCE_ID, user.getId());
            if (DEBUG) Log.d(TAG, "Setting user info");
        } else {
            if (Preferences.DEBUG) Log.d(Preferences.TAG, "Unable to lookup user.");
        }
    }
    
    public static void saveLastUsedDistanceUnit(final Editor editor, int spinnerIndex) {
      editor.putInt(PREFERENCE_LAST_USED_DISTANCE_UNIT, spinnerIndex);
      editor.commit();
    }
    
    public static int getLastUsedDistanceUnit(SharedPreferences prefs) {
      return prefs.getInt(PREFERENCE_LAST_USED_DISTANCE_UNIT, 0);
    }
    
    /**
     * 
     * @param editor
     * @param travelLogEnabled
     * @param travelLogInterval interval in milliseconds
     */
    public static void setTravelLogPreferences(final Editor editor, Boolean travelLogEnabled, String travelLogInterval) {
      editor.putBoolean(PREFERENCE_TRAVEL_LOG_ENABLED, travelLogEnabled);
      editor.putString(PREFERENCE_TRAVEL_LOG_INTERVAL, travelLogInterval);
      editor.commit();
    }
    public static boolean getTravelLogEnabled(SharedPreferences prefs) {
      return prefs.getBoolean(PREFERENCE_TRAVEL_LOG_ENABLED, false);
    }
    public static String getTravelLogInterval(SharedPreferences prefs) {
      return prefs.getString(PREFERENCE_TRAVEL_LOG_INTERVAL, "18000");
    }
}