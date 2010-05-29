package org.cogsurv.droid;

import org.cogsurv.droid.app.TravelLogService;
import org.cogsurv.droid.preferences.Preferences;
import org.cogsurv.droid.util.NotificationsUtil;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
  public static final String  TAG                = "MainActivity";
  public static final boolean DEBUG              = CogSurvDroidSettings.DEBUG;

  private BroadcastReceiver   mLoggedOutReceiver = new BroadcastReceiver() {
                                                   @Override
                                                   public void onReceive(Context context,
                                                       Intent intent) {
                                                     if (DEBUG)
                                                       Log.d(TAG, "onReceive: " + intent);
                                                     finish();
                                                   }
                                                 };

  private View                landmarkVisitButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (DEBUG)
      Log.d(TAG, "onCreate()");
    /* setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL); */
    registerReceiver(mLoggedOutReceiver, new IntentFilter(CogSurvDroid.INTENT_ACTION_LOGGED_OUT));

    // Don't start the main activity if we don't have credentials
    if (!((CogSurvDroid) getApplication()).isReady()) {
      if (DEBUG)
        Log.d(TAG, "Not ready for user.");
      redirectToLoginActivity();
    }

    if (DEBUG)
      Log.d(TAG, "Setting up main activity layout.");

    // if TravelLogService isn't already started, start it
    ((CogSurvDroid) getApplication()).requestStartService();

    setContentView(R.layout.main_activity);

    landmarkVisitButton = this.findViewById(R.id.landmark_visit_button);
    // we only want to enable the landmarkVisitButton after we've downloaded the
    // landmarks
    /* TODO: move this to CogSurvDroid so that it doesn't happen each time we
     * rotate the screen and re-run MainActivity.onCreate */ 
    landmarkVisitButton.setEnabled(false);
    landmarkVisitButton.setFocusable(false);
    landmarkVisitButton.setOnClickListener(this);

    // fetch the landmarks
    if (((CogSurvDroid) getApplication()).isReady()) {
      new ReadLandmarksAsyncTask().execute();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mLoggedOutReceiver);
  }

  private static final int MENU_SWITCH_USER = 1;
  private static final int MENU_SYNC        = 2;
  private static final int MENU_SHUTDOWN    = 3;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    menu.add(Menu.NONE, MENU_SWITCH_USER, 1, R.string.menu_switch_user).setIcon(
        android.R.drawable.ic_menu_edit);

    menu.add(Menu.NONE, MENU_SYNC, 2, R.string.menu_sync)
        .setIcon(android.R.drawable.ic_menu_upload);

    menu.add(Menu.NONE, MENU_SHUTDOWN, 3, R.string.menu_shutdown).setIcon(
        android.R.drawable.ic_menu_close_clear_cancel);

    // MenuUtils.addPreferencesToMenu(this, menu);

    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case MENU_SWITCH_USER:
      if (Preferences.logoutUser( //
          ((CogSurvDroid) getApplication()).getCogSurver(), //
          PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit())) {
        // (1) stop TravelLogService
        // Intent serviceIntent = new Intent(this, TravelLogService.class);
        // stopService(serviceIntent);
        // (2) go to login again
        // redirectToLoginActivity();
        sendBroadcast(new Intent(CogSurvDroid.INTENT_ACTION_LOGGED_OUT));
        return true;
      }
      return true;
      // org.cogsurv.droid.preferences.Preferences.logoutUser(((CogSurvDroid)
      // getApplication()).getCogSurver(),
      // PreferenceManager.getDefaultSharedPreferences(this).edit())
    case MENU_SYNC:
      // TODO: trigger the sync
      return true;
    case MENU_SHUTDOWN:
      // (1) stop TravelLogService
      Intent serviceIntent = new Intent(this, TravelLogService.class);
      stopService(serviceIntent);

      // (2) finish MainActivity
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /*
   * private void initTabHost() { if (mTabHost != null) { throw new
   * IllegalStateException("Trying to intialize already initializd TabHost"); }
   * 
   * mTabHost = getTabHost();
   * 
   * // Places tab mTabHost.addTab(mTabHost.newTabSpec("places") //
   * .setIndicator(getString(R.string.nearby_label),
   * getResources().getDrawable(R.drawable.places_tab)) // the tab icon
   * .setContent(new Intent(this, NearbyVenuesActivity.class)) // The contained
   * activity );
   * 
   * // Friends tab mTabHost.addTab(mTabHost.newTabSpec("friends") //
   * .setIndicator(getString(R.string.checkins_label),
   * getResources().getDrawable(R.drawable.friends_tab)) // the tab // icon
   * .setContent(new Intent(this, FriendsActivity.class)) // The contained
   * activity ); mTabHost.setCurrentTab(0); }
   */
  private void redirectToLoginActivity() {
    setVisible(false);
    Intent intent = new Intent(this, LoginActivity.class);
    intent.setAction(Intent.ACTION_MAIN);
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
    finish();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.landmark_visit_button:
      Log.v("CogSurv", "landmark_visit_button clicked");
      Intent i = new Intent(this, LandmarkVisitSelect.class);
      startActivity(i);
      break;
    }
  }

  /* FETCH LANDMARKS */
  private Dialog mProgressDialog;

  private Dialog showProgressDialog() {
    if (mProgressDialog == null) {
      ProgressDialog dialog = new ProgressDialog(this);
      dialog.setCancelable(true);
      dialog.setIndeterminate(true);
      dialog.setTitle("Syncing");
      dialog.setIcon(android.R.drawable.ic_dialog_info);
      dialog.setMessage("Please wait while we download your landmarks.");
      mProgressDialog = dialog;
    }
    mProgressDialog.show();
    return mProgressDialog;
  }

  private void dismissProgressDialog() {
    try {
      mProgressDialog.dismiss();
    } catch (IllegalArgumentException e) {
      // We don't mind. android cleared it for us.
    }
  }

  private class ReadLandmarksAsyncTask extends AsyncTask<Void, Void, Cursor> {
    private Exception mReason;

    @Override
    public void onPreExecute() {
      showProgressDialog();
    }

    @Override
    protected Cursor doInBackground(Void... params) {
      Cursor cursor = null;
      try {
        cursor = ((CogSurvDroid) getApplication()).readLandmarks(true);
      } catch (Exception e) {
        mReason = e;
      }
      return cursor;
    }

    @Override
    public void onPostExecute(Cursor cursor) {
      if (cursor == null) {
        NotificationsUtil.ToastReasonForFailure(MainActivity.this, mReason);
      } else {
        dismissProgressDialog();
        // now we can enable the landmarkVisitButton
        landmarkVisitButton.setEnabled(true);
        landmarkVisitButton.setFocusable(true);
        // Make sure the caller knows things worked out alright.
        setResult(Activity.RESULT_OK);
      }
    }

    @Override
    protected void onCancelled() {
      setVisible(true);
      dismissProgressDialog();
    }
  }
}
