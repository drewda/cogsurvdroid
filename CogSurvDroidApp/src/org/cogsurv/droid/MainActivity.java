package org.cogsurv.droid;

import org.cogsurv.cogsurver.content.CogSurverProviderUtils;
import org.cogsurv.droid.preferences.Preferences;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    public static final boolean DEBUG = CogSurvDroidSettings.DEBUG;

    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreate()");
        /*setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL);*/
        registerReceiver(mLoggedOutReceiver, new IntentFilter(CogSurvDroid.INTENT_ACTION_LOGGED_OUT));

        // Don't start the main activity if we don't have credentials
        if (!((CogSurvDroid)getApplication()).isReady()) {
            if (DEBUG) Log.d(TAG, "Not ready for user.");
            redirectToLoginActivity();
        }

        if (DEBUG) Log.d(TAG, "Setting up main activity layout.");

        setContentView(R.layout.main_activity);
 
        CogSurverProviderUtils mCogSurverProviderUtils = new CogSurverProviderUtils(this);
        
        // TODO: sometime we need to load landmarks
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
    }
    
    private static final int MENU_SWITCH_USER = 1;
    private static final int MENU_SYNC = 2;
    private static final int MENU_SHUTDOWN = 3;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_SWITCH_USER, 1, R.string.menu_switch_user) 
                .setIcon(android.R.drawable.ic_menu_edit);

        menu.add(Menu.NONE, MENU_SYNC, 2, R.string.menu_sync).setIcon(
                android.R.drawable.ic_menu_upload);

        menu.add(Menu.NONE, MENU_SHUTDOWN, 3, R.string.menu_shutdown).setIcon(android.R.drawable.ic_menu_close_clear_cancel);

        //MenuUtils.addPreferencesToMenu(this, menu);

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
          PreferenceManager.getDefaultSharedPreferences(this).edit())) {
        redirectToLoginActivity();
      }
      return true;
      // org.cogsurv.droid.preferences.Preferences.logoutUser(((CogSurvDroid)
      // getApplication()).getCogSurver(),
      // PreferenceManager.getDefaultSharedPreferences(this).edit())
    case MENU_SYNC:
      // TODO: trigger the sync
      return true;
    case MENU_SHUTDOWN:
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

   /* private void initTabHost() {
        if (mTabHost != null) {
            throw new IllegalStateException("Trying to intialize already initializd TabHost");
        }

        mTabHost = getTabHost();

        // Places tab
        mTabHost.addTab(mTabHost.newTabSpec("places") //
                .setIndicator(getString(R.string.nearby_label),
                        getResources().getDrawable(R.drawable.places_tab)) // the tab icon
                .setContent(new Intent(this, NearbyVenuesActivity.class)) // The contained activity
                );

        // Friends tab
        mTabHost.addTab(mTabHost.newTabSpec("friends") //
                .setIndicator(getString(R.string.checkins_label),
                        getResources().getDrawable(R.drawable.friends_tab)) // the tab
                // icon
                .setContent(new Intent(this, FriendsActivity.class)) // The contained activity
                );
        mTabHost.setCurrentTab(0);
    }
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
}
