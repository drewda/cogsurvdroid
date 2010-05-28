package org.cogsurv.droid;

import java.util.Collections;
import java.util.Date;

import org.cogsurv.cogsurver.content.CogSurverProvider;
import org.cogsurv.cogsurver.content.LandmarksColumns;
import org.cogsurv.cogsurver.types.Group;
import org.cogsurv.cogsurver.types.Landmark;
import org.cogsurv.cogsurver.types.LandmarkVisit;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class LandmarkVisitSelect extends ListActivity {
  private Cursor landmarksCursor;

  private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
          finish();
      }
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.v("CogSurv", "LandmarkVisitSelect.onCreate");
    
    super.onCreate(savedInstanceState);
    registerReceiver(mLoggedOutReceiver, new IntentFilter(CogSurvDroid.INTENT_ACTION_LOGGED_OUT));
    
    // query for person's landmarks
    landmarksCursor = ((CogSurvDroid) getApplication()).readLandmarks(true);

    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, // Context.
        android.R.layout.simple_list_item_1, // Specify the row template to use
        landmarksCursor, // Pass in the cursor to bind to.
        new String[] { LandmarksColumns.NAME }, // Array of cursor columns to bind to.
        new int[] { android.R.id.text1 }); // Parallel array of which template
    // objects to bind to those columns.
    
    adapter.changeCursor(landmarksCursor);
    
    // Bind to our new adapter.
    setListAdapter(adapter);
  }
  
  protected void onListItemClick(ListView parent, View v, int position, long id) {
    Cursor c = (Cursor) getListView().getItemAtPosition(position);
    int startLandmarkId = c.getInt(c.getColumnIndexOrThrow(LandmarksColumns.SERVER_ID));
    
    // assemble estimatesTargetSet
    Group<Landmark> estimatesTargetSet = new Group<Landmark>();
    Landmark landmark;
    Cursor landmarksCursor = ((CogSurvDroid) getApplication()).readLandmarks(false);
    while (landmarksCursor.moveToNext()) {
      landmark = CogSurverProvider.createLandmark(landmarksCursor);
      // we don't want to add the startLandmark to the estimatesTargetSet
      if (landmark.getServerId() != startLandmarkId) {
        estimatesTargetSet.add(landmark);
      }
    }
    Collections.shuffle(estimatesTargetSet);
    ((CogSurvDroid) getApplication()).mEstimatesTargetSet = estimatesTargetSet;
    
    // create landmarkVisit
    LandmarkVisit landmarkVisit = new LandmarkVisit();
    landmarkVisit.setDatetime(new Date());
    landmarkVisit.setLandmarkId(startLandmarkId);
    landmarkVisit = ((CogSurvDroid) getApplication()).recordLandmarkVisit(landmarkVisit);
    
    Log.v(CogSurvDroid.TAG, "landmark selected: " + startLandmarkId + "; number of targets: " + estimatesTargetSet.size());
    
    if (estimatesTargetSet.size() > 0) {
      Intent i = new Intent(this, LandmarkVisitEstimates.class);
      i.putExtra("startLandmarkId", startLandmarkId);
      i.putExtra("landmarkVisitId", landmarkVisit.getServerId());
      i.putExtra("startLandmarkName", c.getString(c.getColumnIndexOrThrow(LandmarksColumns.NAME)));
      startActivity(i);
    }
    else {
      Toast.makeText(this, "Error: There are no other landmarks to estimate directions and distances toward.", Toast.LENGTH_LONG).show();
    }
    finish();
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mLoggedOutReceiver);
  }
}