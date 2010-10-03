package org.cogsurv.droid;

import java.text.DecimalFormat;
import java.util.Date;

import org.cogsurv.cogsurver.types.DirectionDistanceEstimate;
import org.cogsurv.cogsurver.types.Landmark;
import org.cogsurv.droid.util.NotificationsUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class LandmarkVisitEstimates extends Activity implements OnClickListener {
  private int startLandmarkId;
  private int landmarkVisitId;
  private Landmark currentTargetLandmark;

  private double distanceNumber = 0.00;
  private String distanceUnits;

  private DecimalFormat decimalFormat;

  private TextView targetLandmarkHeader;
  // private TextView targetLandmarkCity;
  private EditText distanceNumberEditText;
  private Spinner distanceUnitsSpinner;

  private Button recordEstimatesButton;

  private boolean waveDialogShown = false;

  /* compass */
  private SensorManager sensorManager;
  private float[] compassValues;
  private SensorEventListener compassEventListener = new SensorEventListener() {
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    public void onSensorChanged(SensorEvent event) {
      if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
        Toast
            .makeText(
                getBaseContext(),
                "Compass is getting interference. Please wave the phone around again.",
                Toast.LENGTH_LONG).show();
      } else {
        compassValues = event.values;
      }
    }
  };

  private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      finish();
    }
  };
  /* adjust compass from magnetic to true north */
  private GeomagneticField geomagneticfield;
  private Location locationFix;
  private LocationManager locationManager;
  private LocationListener locationListener = new LocationListener() {
    public void onLocationChanged(Location loc) {
      locationFix = new Location(loc);
      geomagneticfield = new GeomagneticField(
          (float) locationFix.getLatitude(),
          (float) locationFix.getLongitude(),
          (float) locationFix.getAltitude(), new Date().getTime());
      recordEstimatesButton.setFocusable(true);
      recordEstimatesButton.setClickable(true);
      Toast.makeText(LandmarkVisitEstimates.this, "LOCATION",
          Toast.LENGTH_SHORT).show();
    }

    public void onProviderDisabled(String arg0) {
    }

    public void onProviderEnabled(String arg0) {
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }
  };

  private DirectionDistanceEstimate directionDistanceEstimate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    registerReceiver(mLoggedOutReceiver, new IntentFilter(
        CogSurvDroid.INTENT_ACTION_LOGGED_OUT));

    /* set up compass */
    sensorManager = (SensorManager) this
        .getSystemService(Context.SENSOR_SERVICE);
    Sensor compass = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    sensorManager.registerListener(compassEventListener, compass,
        SensorManager.SENSOR_DELAY_FASTEST);

    /* set up geomagneticfield */
    // locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
    // 0, 0, locationListener);

    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    // Try to get the last GPS based location
    locationFix = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

    // Fall back to cell tower based location if no prior GPS location
    if (locationFix == null) {
      locationFix = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
    geomagneticfield = new GeomagneticField((float) locationFix.getLatitude(),
        (float) locationFix.getLongitude(), (float) locationFix.getAltitude(),
        new Date().getTime());

    setContentView(R.layout.landmark_visit_estimates);

    /* extras attached to intent by LandmarkVisitSelect */
    Bundle extras = this.getIntent().getExtras();
    startLandmarkId = extras.getInt("startLandmarkId");
    landmarkVisitId = extras.getInt("landmarkVisitId");

    // set up view for survey estimate
    currentTargetLandmark = ((CogSurvDroid) getApplication()).mEstimatesTargetSet
        .get(0);
    targetLandmarkHeader = (TextView) findViewById(R.id.target_landmark_header);
    findViewById(R.id.estimates_feedback).setVisibility(View.GONE);

    if (currentTargetLandmark.getName() == CogSurvDroidSettings.POINT_TO_NORTH_COMMAND) {
      ((TextView) findViewById(R.id.main_prompt))
          .setText(R.string.direction_estimate_prompt);
      findViewById(R.id.distance_prompt).setVisibility(View.GONE);
      findViewById(R.id.distance_entry).setVisibility(View.GONE);
      targetLandmarkHeader.setText("north");
    } else {

      /*
       * targetLandmarkCity = (TextView)
       * findViewById(R.id.target_landmark_city);
       */
      targetLandmarkHeader.setText(currentTargetLandmark.getName());
      /*
       * targetLandmarkCity.setText('('
       * +currentSurveyingTarget.getVenue().getCity() + ')');
       */
    }

    // units spinner
    distanceUnitsSpinner = (Spinner) findViewById(R.id.distance_units_spinner);
    ArrayAdapter<CharSequence> distanceUnitsAdapter = ArrayAdapter
        .createFromResource(this, R.array.distance_units_array,
            android.R.layout.simple_spinner_item);
    distanceUnitsSpinner.setAdapter(distanceUnitsAdapter);
    distanceUnitsSpinner
        .setOnItemSelectedListener(new OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view,
              int position, long id) {
            distanceUnits = parent.getItemAtPosition(position).toString();
          }

          @Override
          public void onNothingSelected(AdapterView<?> arg0) {
          }
        });

    // register buttons
    recordEstimatesButton = (Button) findViewById(R.id.record_estimates_button);
    Button distancePlusHundredButton = (Button) findViewById(R.id.distance_plus_hundred_button);
    Button distancePlusTenButton = (Button) findViewById(R.id.distance_plus_ten_button);
    Button distancePlusOneButton = (Button) findViewById(R.id.distance_plus_one_button);
    Button distancePlusTenthButton = (Button) findViewById(R.id.distance_plus_tenth_button);
    Button distanceMinusHundredButton = (Button) findViewById(R.id.distance_minus_hundred_button);
    Button distanceMinusTenButton = (Button) findViewById(R.id.distance_minus_ten_button);
    Button distanceMinusOneButton = (Button) findViewById(R.id.distance_minus_one_button);
    Button distanceMinusTenthButton = (Button) findViewById(R.id.distance_minus_tenth_button);
    recordEstimatesButton.setOnClickListener(this);
    distancePlusHundredButton.setOnClickListener(this);
    distancePlusTenButton.setOnClickListener(this);
    distancePlusOneButton.setOnClickListener(this);
    distancePlusTenthButton.setOnClickListener(this);
    distanceMinusHundredButton.setOnClickListener(this);
    distanceMinusTenButton.setOnClickListener(this);
    distanceMinusOneButton.setOnClickListener(this);
    distanceMinusTenthButton.setOnClickListener(this);

    distanceNumberEditText = (EditText) findViewById(R.id.distance_number_edit_text);

    // http://www.velocityreviews.com/forums/t139008-java-double-precision.html
    decimalFormat = new DecimalFormat("#####.#");

    if (waveDialogShown != true) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(
          "Wave the phone around in a figure 8. This resets the compass.")
          .setCancelable(false).setPositiveButton("Done",
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  waveDialogShown = true;
                  dialog.cancel();
                }
              });
      AlertDialog alert = builder.create();
      alert.show();
    }
  }

  @Override
  protected void onDestroy() {
    unregisterReceiver(mLoggedOutReceiver);
    super.onDestroy();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.record_estimates_button:
      directionDistanceEstimate = new DirectionDistanceEstimate();
      /* point-to-north, which for now is always the last target */
      if (currentTargetLandmark.getName() == CogSurvDroidSettings.POINT_TO_NORTH_COMMAND) {
        directionDistanceEstimate.setStartLandmarkId(startLandmarkId);
        directionDistanceEstimate.setDatetime(new Date());
        directionDistanceEstimate.setDirectionEstimate(compassValues[0]
            + geomagneticfield.getDeclination());
        directionDistanceEstimate.setLandmarkVisitId(landmarkVisitId);
      }
      /* a normal estimate-to-landmark */
      else {
        // (1) record this directionDistanceEstimate
        directionDistanceEstimate.setStartLandmarkId(startLandmarkId);
        directionDistanceEstimate.setTargetLandmarkId(currentTargetLandmark
            .getServerId());
        directionDistanceEstimate.setDatetime(new Date());
        directionDistanceEstimate.setDirectionEstimate(compassValues[0]
            + geomagneticfield.getDeclination());
        directionDistanceEstimate.setDistanceEstimate(distanceNumber);
        directionDistanceEstimate.setDistanceEstimateUnits(distanceUnits);
        directionDistanceEstimate.setLandmarkVisitId(landmarkVisitId);
      }
      new RecordDirectionDistanceEstimateAsyncTask()
          .execute(directionDistanceEstimate);
      // (2) go to the next target
      // will be handled in
      // RecordDirectionDistanceEstimateAsyncTask.onPostExecute
      break;
    case R.id.distance_plus_hundred_button:
      distanceNumber = distanceNumber + 100;
      distanceNumberEditText.setText(decimalFormat.format(distanceNumber));
      break;
    case R.id.distance_plus_ten_button:
      distanceNumber = distanceNumber + 10;
      distanceNumberEditText.setText(decimalFormat.format(distanceNumber));
      break;
    case R.id.distance_plus_one_button:
      distanceNumber = distanceNumber + 1;
      distanceNumberEditText.setText(decimalFormat.format(distanceNumber));
      break;
    case R.id.distance_plus_tenth_button:
      distanceNumber = distanceNumber + 0.1;
      distanceNumberEditText.setText(decimalFormat.format(distanceNumber));
      break;
    case R.id.distance_minus_hundred_button:
      if (distanceNumber >= 100) {
        distanceNumber = distanceNumber - 100;
        distanceNumberEditText.setText(decimalFormat.format(distanceNumber));
      }
      break;
    case R.id.distance_minus_ten_button:
      if (distanceNumber >= 10) {
        distanceNumber = distanceNumber - 10;
        distanceNumberEditText.setText(decimalFormat.format(distanceNumber));
      }
      break;
    case R.id.distance_minus_one_button:
      if (distanceNumber >= 1) {
        distanceNumber = distanceNumber - 1;
        distanceNumberEditText.setText(decimalFormat.format(distanceNumber));
      }
      break;
    case R.id.distance_minus_tenth_button:
      if (distanceNumber >= 0.1) {
        distanceNumber = distanceNumber - 0.1;
        distanceNumberEditText.setText(decimalFormat.format(distanceNumber));
      }
      break;
    /*
     * case R.id.done_surveying_button: this.finish(); break;
     */
    }
  }

  /* trying to deal with screen rotation */
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  /* RECORD DIRECTION DISTANCE ESTIMATE */
  private Dialog mProgressDialog;

  private Dialog showProgressDialog() {
    if (mProgressDialog == null) {
      ProgressDialog dialog = new ProgressDialog(this);
      dialog.setCancelable(true);
      dialog.setIndeterminate(true);
      dialog.setTitle("Syncing");
      dialog.setIcon(android.R.drawable.ic_dialog_info);
      dialog.setMessage("Please wait while we record your estimate.");
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

  private class RecordDirectionDistanceEstimateAsyncTask extends
      AsyncTask<DirectionDistanceEstimate, Void, DirectionDistanceEstimate> {
    private Exception mReason;

    @Override
    public void onPreExecute() {
      showProgressDialog();
    }

    @Override
    protected DirectionDistanceEstimate doInBackground(
        DirectionDistanceEstimate... params) {
      DirectionDistanceEstimate directionDistanceEstimate = null;
      try {
        directionDistanceEstimate = ((CogSurvDroid) getApplication())
            .recordDirectionDistanceEstimate(params[0]);
      } catch (Exception e) {
        mReason = e;
      }
      return directionDistanceEstimate;
    }

    @Override
    public void onPostExecute(
        DirectionDistanceEstimate directionDistanceEstimate) {
      if (directionDistanceEstimate == null) {
        NotificationsUtil.ToastReasonForFailure(LandmarkVisitEstimates.this,
            mReason);
      } else {
        dismissProgressDialog();

        ((CogSurvDroid) getApplication()).mEstimatesTargetSet.remove(0);

        if (((CogSurvDroid) getApplication()).mEstimatesTargetSet.size() > 0) {
          Toast.makeText(LandmarkVisitEstimates.this,
              "Great guess! Please try another.", Toast.LENGTH_LONG).show();

          currentTargetLandmark = ((CogSurvDroid) getApplication()).mEstimatesTargetSet
              .get(0);
          /* point-to-north, which for now is always the last target */
          if (currentTargetLandmark.getName() == CogSurvDroidSettings.POINT_TO_NORTH_COMMAND) {
            ((TextView) findViewById(R.id.main_prompt))
                .setText(R.string.direction_estimate_prompt);
            findViewById(R.id.distance_prompt).setVisibility(View.GONE);
            findViewById(R.id.distance_entry).setVisibility(View.GONE);
            targetLandmarkHeader.setText("north");
          }
          /* a normal estimate-to-landmark */
          else {
            targetLandmarkHeader.setText(currentTargetLandmark.getName());
            /*
             * targetLandmarkCity.setText('(' +
             * currentSurveyingTarget.getVenue().getCity() + ')');
             */
            distanceNumber = 0;
            distanceNumberEditText.setText(String.valueOf(distanceNumber));

            /*
             * findViewById(R.id.estimates_feedback).setVisibility(View
             * .VISIBLE);
             */
            /*
             * Button doneSurveyingButton = (Button)
             * findViewById(R.id.done_surveying_button);
             */
            /* doneSurveyingButton.setOnClickListener(this); */
          }
        } else {
          Toast.makeText(LandmarkVisitEstimates.this,
              "Good work! You've done all the landmarks.", Toast.LENGTH_LONG)
              .show();
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
