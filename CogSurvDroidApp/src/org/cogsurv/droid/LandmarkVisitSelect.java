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
  private Group<Landmark>   estimatesTargetSet;
  private Cursor            c;
  private LandmarkVisit     landmarkVisit;

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

    // query for person's landmarks
    landmarksCursor = ((CogSurvDroid) getApplication()).readLandmarks(false);

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

    // assemble estimatesTargetSet
    estimatesTargetSet = new Group<Landmark>();
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
    // add point-to-north command
    landmark = new Landmark();
    landmark.setName(CogSurvDroidSettings.POINT_TO_NORTH_COMMAND);
    estimatesTargetSet.add(landmark);
    // set the estimatesTargetSet, which LandmarkVisitEstimates will pick up
    ((CogSurvDroid) getApplication()).mEstimatesTargetSet = estimatesTargetSet;

    // create landmarkVisit
    landmarkVisit = new LandmarkVisit();
    landmarkVisit.setDatetime(new Date());
    landmarkVisit.setLandmarkId(startLandmarkId);
    new RecordLandmarkVisitAsyncTask().execute(landmarkVisit);

    landmarkVisit = ((CogSurvDroid) getApplication()).recordLandmarkVisit(landmarkVisit);

    Log.v(CogSurvDroid.TAG, "landmark selected: " + startLandmarkId + "; number of targets: "
        + estimatesTargetSet.size());
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mLoggedOutReceiver);
  }

  /* RECORD LANDMARK VISIT */
  private Dialog mProgressDialog;

  private Dialog showProgressDialog() {
    if (mProgressDialog == null) {
      ProgressDialog dialog = new ProgressDialog(this);
      dialog.setCancelable(true);
      dialog.setIndeterminate(true);
      dialog.setTitle("Syncing");
      dialog.setIcon(android.R.drawable.ic_dialog_info);
      dialog.setMessage("Please wait while we record your landmark visit.");
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

  private class RecordLandmarkVisitAsyncTask extends AsyncTask<LandmarkVisit, Void, LandmarkVisit> {
    private Exception mReason;

    @Override
    public void onPreExecute() {
      showProgressDialog();
    }

    @Override
    protected LandmarkVisit doInBackground(LandmarkVisit... params) {
      LandmarkVisit landmarkVisit = null;
      try {
        landmarkVisit = ((CogSurvDroid) getApplication()).recordLandmarkVisit(params[0]);
      } catch (Exception e) {
        mReason = e;
      }
      return landmarkVisit;
    }

    @Override
    public void onPostExecute(LandmarkVisit landmarkVisit) {
      if (landmarkVisit == null) {
        NotificationsUtil.ToastReasonForFailure(LandmarkVisitSelect.this, mReason);
      } else {
        dismissProgressDialog();

        if (estimatesTargetSet.size() > 0) {
          Intent i = new Intent(LandmarkVisitSelect.this, LandmarkVisitEstimates.class);
          i.putExtra("startLandmarkId", startLandmarkId);
          i.putExtra("landmarkVisitId", landmarkVisit.getServerId());
          i.putExtra("startLandmarkName", c.getString(c
              .getColumnIndexOrThrow(LandmarksColumns.NAME)));
          startActivity(i);
          finish();
        } else {
          Toast.makeText(LandmarkVisitSelect.this,
              "Error: There are no other landmarks to estimate directions and distances toward.",
              Toast.LENGTH_LONG).show();
          finish();
        }

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