package org.cogsurv.droid;

import java.util.Collections;
import java.util.Date;

import org.cogsurv.cogsurver.content.CogSurverProvider;
import org.cogsurv.cogsurver.content.LandmarksColumns;
import org.cogsurv.cogsurver.types.Group;
import org.cogsurv.cogsurver.types.Landmark;
import org.cogsurv.cogsurver.types.LandmarkVisit;
import org.cogsurv.droid.util.NotificationsUtil;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class LandmarkVisitSelect extends ListActivity {
  private Cursor            landmarksCursor;

  private int               startLandmarkId;
  private Cursor            c;

  private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
                                                 @Override
                                                 public void onReceive(Context context,
                                                     Intent intent) {
                                                   finish();
                                                 }
                                               };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.v("CogSurv", "LandmarkVisitSelect.onCreate");

    super.onCreate(savedInstanceState);
    registerReceiver(mLoggedOutReceiver, new IntentFilter(CogSurvDroid.INTENT_ACTION_LOGGED_OUT));
    
    if (((CogSurvDroid)getApplication()).landmarksLoaded != true) {
      Toast.makeText(this, "Landmarks haven't been loaded yet.", Toast.LENGTH_LONG);
      finish();
    }
    
    setContentView(R.layout.landmark_visit_select);

    // query for person's landmarks
    landmarksCursor = ((CogSurvDroid) getApplication()).readLandmarks(false);
    startManagingCursor(landmarksCursor);

    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, // Context.
        android.R.layout.simple_list_item_1, // Specify the row template to use
        landmarksCursor, // Pass in the cursor to bind to.
        new String[] { LandmarksColumns.NAME }, // Array of cursor columns to
        // bind to.
        new int[] { android.R.id.text1 }); // Parallel array of which template
    // objects to bind to those columns.

    adapter.changeCursor(landmarksCursor);

    // Bind to our new adapter.
    setListAdapter(adapter);
  }

  protected void onListItemClick(ListView parent, View v, int position, long id) {
    c = (Cursor) getListView().getItemAtPosition(position);
    startLandmarkId = c.getInt(c.getColumnIndexOrThrow(LandmarksColumns.SERVER_ID));
    
    Intent i = new Intent(LandmarkVisitSelect.this, LandmarkVisitEstimates.class);
    i.putExtra("startLandmarkId", startLandmarkId);
    //i.putExtra("startLandmarkName", c.getString(c.getColumnIndexOrThrow(LandmarksColumns.NAME)));
    startActivity(i);
    finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mLoggedOutReceiver);
  }
}