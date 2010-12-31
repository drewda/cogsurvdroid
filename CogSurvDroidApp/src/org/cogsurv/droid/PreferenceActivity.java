package org.cogsurv.droid;

import org.cogsurv.cogsurver.types.User;
import org.cogsurv.droid.app.TravelLogService;
import org.cogsurv.droid.preferences.Preferences;
import org.cogsurv.droid.util.NotificationsUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class PreferenceActivity extends android.preference.PreferenceActivity
    implements OnSharedPreferenceChangeListener {
  private static final String TAG = "PreferenceActivity";

  private static final boolean DEBUG = CogSurvDroidSettings.DEBUG;

  private SharedPreferences mPrefs;

  private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (DEBUG)
        Log.d(TAG, "onReceive: " + intent);
      finish();
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    registerReceiver(mLoggedOutReceiver, new IntentFilter(
        CogSurvDroid.INTENT_ACTION_LOGGED_OUT));

    addPreferencesFromResource(R.xml.preferences);
    mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
  }

  @Override
  protected void onResume() {
    super.onResume();

    getPreferenceScreen().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();

    // Unregister the listener whenever a key changes
    getPreferenceScreen().getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mLoggedOutReceiver);
  }

  @Override
  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
      Preference preference) {
    if (DEBUG)
      Log.d(TAG, "onPreferenceTreeClick");
    String key = preference.getKey();
    if (Preferences.PREFERENCE_LOGOUT.equals(key)) {
      mPrefs.edit().clear().commit();
      ((CogSurvDroid) getApplication()).getCogSurver().setCredentials(null,
          null);

      Intent intent = new Intent(this, LoginActivity.class);
      intent.setAction(Intent.ACTION_MAIN);
      intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
          | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
          | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      sendBroadcast(new Intent(CogSurvDroid.INTENT_ACTION_LOGGED_OUT));
    }

    return true;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
      String key) {
    Boolean needToUpdateServer = false;
    if (Preferences.PREFERENCE_TRAVEL_LOG_ENABLED.equals(key)) {
      needToUpdateServer = true;
      if (Preferences.getTravelLogEnabled(mPrefs)) {
        ((CogSurvDroid) getApplication()).requestStartService();
      } else {
        Intent serviceIntent = new Intent(this, TravelLogService.class);
        stopService(serviceIntent);
      }
    } else if (Preferences.PREFERENCE_TRAVEL_LOG_INTERVAL.equals(key)) {
      needToUpdateServer = true;
      // restart travel log service
      Intent serviceIntent = new Intent(this, TravelLogService.class);
      stopService(serviceIntent);
      ((CogSurvDroid) getApplication()).requestStartService();
    }

    if (needToUpdateServer) {
      new UpdateUserPreferencesAsyncTask().execute();
    }

  }
  
  public class UpdateUserPreferencesAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private Exception mReason;

    @Override
    public void onPreExecute() {
      ((CogSurvDroid) getApplication()).showProgressDialog(
          "Please wait while we update your preferences on the server.", 
          "Updating",
          PreferenceActivity.this);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      User user = ((CogSurvDroid) getApplication()).getCurrentUser();
      user.setTravelLogEnabled(Preferences.getTravelLogEnabled(mPrefs));
      user.setTravelLogInterval(Integer.valueOf(Preferences
          .getTravelLogInterval(mPrefs)));
      return ((CogSurvDroid) getApplication()).updateUserPreferences(user);
    }

    @Override
    public void onPostExecute(Boolean success) {
      if (!success) {
        NotificationsUtil.ToastReasonForFailure(PreferenceActivity.this, mReason);
      } else {
        ((CogSurvDroid) getApplication()).dismissProgressDialog();
        setResult(Activity.RESULT_OK);
      }
    }

    @Override
    protected void onCancelled() {
      setVisible(true);
      ((CogSurvDroid) getApplication()).dismissProgressDialog();
    }
  }

}