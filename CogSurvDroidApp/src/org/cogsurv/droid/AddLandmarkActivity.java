package org.cogsurv.droid;

import org.cogsurv.cogsurver.types.Landmark;
import org.cogsurv.droid.error.LocationException;
import org.cogsurv.droid.util.NotificationsUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddLandmarkActivity extends Activity implements OnClickListener{
  private EditText newLandmarkNameEditText;
  
  private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      finish();
    }
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    registerReceiver(mLoggedOutReceiver, new IntentFilter(
        CogSurvDroid.INTENT_ACTION_LOGGED_OUT));
    
    setContentView(R.layout.add_landmark_activity);
    
    newLandmarkNameEditText = (EditText) findViewById(R.id.new_landmark_name_edit_text);
    Button markItButton = (Button) findViewById(R.id.mark_it_button);
    markItButton.setOnClickListener(this);
  }
  
  @Override
  public void onResume() {
    ((CogSurvDroid) getApplication()).requestLocationUpdates(true);
      super.onResume();
  }

  @Override
  public void onPause() {
    ((CogSurvDroid) getApplication()).removeLocationUpdates();
      super.onPause();
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mLoggedOutReceiver);
  }

  @Override
  public void onClick(View v) {
    switch(v.getId()) {
    case R.id.mark_it_button:
      new UploadLandmarkAsyncTask().execute();
      break;
    }
  }
  
  public class UploadLandmarkAsyncTask extends AsyncTask<Void, Void, Landmark> {
    private Exception mReason;

    @Override
    public void onPreExecute() {
      ((CogSurvDroid) getApplication()).showProgressDialog(
          "Please wait while we upload your landmark.", 
          "Uploading",
          AddLandmarkActivity.this);
    }

    @Override
    protected Landmark doInBackground(Void... params) {
      Landmark landmark = new Landmark();
      landmark.setName(newLandmarkNameEditText.getText().toString());
      try {
        Location location = ((CogSurvDroid) getApplication()).getLastKnownLocationOrThrow();
        landmark.setLatitude(location.getLatitude());
        landmark.setLongitude(location.getLongitude());
      } catch (LocationException e) {
        e.printStackTrace();
      }
      landmark = ((CogSurvDroid) getApplication()).markNewLandmark(landmark);
      return landmark;
    }

    @Override
    public void onPostExecute(Landmark landmark) {
      if (landmark == null) {
        NotificationsUtil.ToastReasonForFailure(AddLandmarkActivity.this, mReason);
      } else {
        ((CogSurvDroid) getApplication()).dismissProgressDialog();
        setResult(Activity.RESULT_OK);
        Toast
        .makeText(
            getBaseContext(),
            "Landmark has been marked.",
            Toast.LENGTH_LONG).show();
        Intent i = new Intent(AddLandmarkActivity.this, LandmarkVisitEstimates.class);
        i.putExtra("startLandmarkId", landmark.getServerId());
        startActivity(i);
        finish();
      }
    }

    @Override
    protected void onCancelled() {
      setVisible(true);
      ((CogSurvDroid) getApplication()).dismissProgressDialog();
    }
  }
}
