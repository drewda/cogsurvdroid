package org.cogsurv.droid;

import java.util.Date;

import org.cogsurv.cogsurver.types.DirectionDistanceEstimate;
import org.cogsurv.cogsurver.types.Landmark;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class LandmarkVisitEstimates extends Activity implements OnClickListener {
  private int                 startLandmarkId;
  private int                 landmarkVisitId;
  private Landmark            currentTargetLandmark;

  private int                 distanceWholeNumber  = 0;
  private int                 distanceTenthNumber  = 0;
  private String              distanceUnits;

  private TextView            targetLandmarkHeader;
  /*private TextView            targetLandmarkCity;*/
  private EditText            distanceWholeNumberEditText;
  private EditText            distanceTenthNumberEditText;
  private Spinner             distanceUnitsSpinner;

  /* compass */
  private SensorManager       sensorManager;
  private float[]             compassValues;
  private SensorEventListener compassEventListener = new SensorEventListener() {
                                                     public void onAccuracyChanged(Sensor arg0,
                                                         int arg1) {
                                                     }

                                                     public void onSensorChanged(SensorEvent event) {
                                                       compassValues = event.values;
                                                     }
                                                   };

  private BroadcastReceiver   mLoggedOutReceiver   = new BroadcastReceiver() {
                                                     @Override
                                                     public void onReceive(Context context,
                                                         Intent intent) {
                                                       finish();
                                                     }
                                                   };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    registerReceiver(mLoggedOutReceiver, new IntentFilter(CogSurvDroid.INTENT_ACTION_LOGGED_OUT));

    /* set up compass */
    sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
    Sensor compass = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    sensorManager.registerListener(compassEventListener, compass,
        SensorManager.SENSOR_DELAY_FASTEST);

    setContentView(R.layout.landmark_visit_estimates);

    /* extras attached to intent by LandmarkVisitSelect */
    Bundle extras = this.getIntent().getExtras();
    startLandmarkId = extras.getInt("startLandmarkId");
    landmarkVisitId = extras.getInt("landmarkVisitId");

    // set up view for first survey estimate
    currentTargetLandmark = ((CogSurvDroid) getApplication()).mEstimatesTargetSet.get(0);
    findViewById(R.id.estimates_feedback).setVisibility(View.GONE);
    targetLandmarkHeader = (TextView) findViewById(R.id.target_landmark_header);
    /* targetLandmarkCity = (TextView) findViewById(R.id.target_landmark_city); */
    targetLandmarkHeader.setText(currentTargetLandmark.getName());
    /*
     * targetLandmarkCity.setText('(' +
     * currentSurveyingTarget.getVenue().getCity() + ')');
     */

    // units spinner
    distanceUnitsSpinner = (Spinner) findViewById(R.id.distance_units_spinner);
    ArrayAdapter<CharSequence> distanceUnitsAdapter = ArrayAdapter.createFromResource(this,
        R.array.distance_units_array, android.R.layout.simple_spinner_item);
    distanceUnitsSpinner.setAdapter(distanceUnitsAdapter);
    distanceUnitsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        distanceUnits = parent.getItemAtPosition(position).toString();
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });

    // register buttons
    Button recordEstimatesButton = (Button) findViewById(R.id.record_estimates_button);
    Button distanceWholeNumberPlusButton = (Button) findViewById(R.id.distance_whole_number_plus_button);
    Button distanceWholeNumberMinusBotton = (Button) findViewById(R.id.distance_whole_number_minus_button);
    Button distanceTenthNumberPlusBotton = (Button) findViewById(R.id.distance_tenth_number_plus_button);
    Button distanceTenthNumberMinusButton = (Button) findViewById(R.id.distance_tenth_number_minus_button);
    recordEstimatesButton.setOnClickListener(this);
    distanceWholeNumberPlusButton.setOnClickListener(this);
    distanceWholeNumberMinusBotton.setOnClickListener(this);
    distanceTenthNumberPlusBotton.setOnClickListener(this);
    distanceTenthNumberMinusButton.setOnClickListener(this);

    distanceWholeNumberEditText = (EditText) findViewById(R.id.distance_whole_number_edit_text);
    distanceTenthNumberEditText = (EditText) findViewById(R.id.distance_tenth_number_edit_text);

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
      // (1) record this directionDistanceEstimate
      Double distanceEstimate = distanceWholeNumber + (distanceTenthNumber * .1);
      DirectionDistanceEstimate directionDistanceEstimate = new DirectionDistanceEstimate();
      directionDistanceEstimate.setStartLandmarkId(startLandmarkId);
      directionDistanceEstimate.setTargetLandmarkId(currentTargetLandmark.getServerId());
      directionDistanceEstimate.setDatetime(new Date());
      directionDistanceEstimate.setDirectionEstimate(compassValues[0]);
      directionDistanceEstimate.setDistanceEstimate(distanceEstimate);
      directionDistanceEstimate.setDistanceEstimateUnits(distanceUnits);
      directionDistanceEstimate.setLandmarkVisitId(landmarkVisitId);

      ((CogSurvDroid) getApplication()).recordDirectionDistanceEstimate(directionDistanceEstimate);

      // (2) go to the next target
      ((CogSurvDroid) getApplication()).mEstimatesTargetSet.remove(0);

      if (((CogSurvDroid) getApplication()).mEstimatesTargetSet.size() > 0) {
        currentTargetLandmark = ((CogSurvDroid) getApplication()).mEstimatesTargetSet.get(0);
        targetLandmarkHeader.setText(currentTargetLandmark.getName());
        /*targetLandmarkCity.setText('(' + currentSurveyingTarget.getVenue().getCity() + ')');*/
        distanceWholeNumber = 0;
        distanceTenthNumber = 0;
        distanceWholeNumberEditText.setText(String.valueOf(distanceWholeNumber));
        distanceTenthNumberEditText.setText(String.valueOf(distanceTenthNumber));

        findViewById(R.id.estimates_feedback).setVisibility(View.VISIBLE);
        /*Button doneSurveyingButton = (Button) findViewById(R.id.done_surveying_button);*/
        /*doneSurveyingButton.setOnClickListener(this);*/
      } else {
        this.finish();
      }

      break;
    case R.id.distance_whole_number_plus_button:
      ++distanceWholeNumber;
      distanceWholeNumberEditText.setText(String.valueOf(distanceWholeNumber));
      break;
    case R.id.distance_whole_number_minus_button:
      if (distanceWholeNumber > 0)
        --distanceWholeNumber;
      distanceWholeNumberEditText.setText(String.valueOf(distanceWholeNumber));
      break;
    case R.id.distance_tenth_number_plus_button:
      if (distanceTenthNumber < 9)
        distanceTenthNumber = distanceTenthNumber + 1;
      distanceTenthNumberEditText.setText(String.valueOf(distanceTenthNumber));
      break;
    case R.id.distance_tenth_number_minus_button:
      if (distanceTenthNumber > 0)
        distanceTenthNumber = distanceTenthNumber - 1;
      distanceTenthNumberEditText.setText(String.valueOf(distanceTenthNumber));
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
}
