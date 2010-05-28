package org.cogsurv.cogsurver;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.cogsurv.cogsurver.error.CogSurvCredentialsException;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvException;
import org.cogsurv.cogsurver.http.AbstractHttpApi;
import org.cogsurv.cogsurver.http.HttpApi;
import org.cogsurv.cogsurver.parsers.DirectionDistanceEstimateParser;
import org.cogsurv.cogsurver.parsers.GroupParser;
import org.cogsurv.cogsurver.parsers.LandmarkParser;
import org.cogsurv.cogsurver.parsers.LandmarkVisitParser;
import org.cogsurv.cogsurver.parsers.TravelFixParser;
import org.cogsurv.cogsurver.parsers.UserParser;
import org.cogsurv.cogsurver.types.DirectionDistanceEstimate;
import org.cogsurv.cogsurver.types.Group;
import org.cogsurv.cogsurver.types.Landmark;
import org.cogsurv.cogsurver.types.LandmarkVisit;
import org.cogsurv.cogsurver.types.TravelFix;
import org.cogsurv.cogsurver.types.User;
import org.cogsurv.cogsurver.http.HttpApiWithBasicAuth;

import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * based on code by Joe LaPenna (joe@joelapenna.com)
 */
class CogSurverHttpApiV1 {
    private static final Logger LOG = Logger
            .getLogger(CogSurverHttpApiV1.class.getCanonicalName());
    private static final boolean DEBUG = CogSurver.DEBUG;

    private static final String URL_API_USERS = "/users.xml";
    private static final String URL_API_TRAVEL_FIXES = "/travel_fixes.xml";
    private static final String URL_API_LANDMARKS = "/landmarks.xml";
    private static final String URL_API_LANDMARK_VISITS = "/landmark_visits.xml";
    private static final String URL_API_DIRECTION_DISTANCE_ESTIMATES = "/direction_distance_estimates.xml";

    private final DefaultHttpClient mHttpClient = AbstractHttpApi.createHttpClient();
    private HttpApi mHttpApi;

    private final String mApiBaseUrl;
    private final AuthScope mAuthScope;

    SimpleDateFormat iso8601DatetimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
    
    public CogSurverHttpApiV1(String domain, String clientVersion) {
        mApiBaseUrl = "http://" + domain + "/api";
        mAuthScope = new AuthScope(domain, 80);

        mHttpApi = new HttpApiWithBasicAuth(mHttpClient, clientVersion);
    }

    void setCredentials(String email, String password) {
        if (email == null || email.length() == 0 || password == null || password.length() == 0) {
            if (DEBUG) LOG.log(Level.FINE, "Clearing Credentials");
            Log.d("CogSurverHttpApiV1", "Clearing Credentials");
            mHttpClient.getCredentialsProvider().clear();
        } else {
            if (DEBUG) LOG.log(Level.FINE, "Setting E-mail/Password: " + email + "/******");
            Log.d("CogSurverHttpApiV1", "Setting E-mail/Password: " + email + "/" + password);
            mHttpClient.getCredentialsProvider().setCredentials(mAuthScope,
                    new UsernamePasswordCredentials(email, password));
        }
    }

    public boolean hasCredentials() {
        return mHttpClient.getCredentialsProvider().getCredentials(mAuthScope) != null;
    }
   
    @SuppressWarnings("unchecked")
    User readUser() throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_USERS));
        Log.d("CogSurver", "readUser: " + httpGet.toString());
        Group<User> users = (Group<User>) mHttpApi.doHttpRequest(httpGet, new GroupParser(new UserParser()));
        return users.get(0);
    }
    
    Landmark createLandmark(Landmark landmark)
            throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_LANDMARKS), 
                new BasicNameValuePair("landmark[foursquare-venue-id]", landmark.getFoursquareVenueId()),
                new BasicNameValuePair("landmark[name]", landmark.getName()),
                new BasicNameValuePair("landmark[address]", landmark.getAddress()),
                new BasicNameValuePair("landmark[city]", landmark.getCity()),
                new BasicNameValuePair("landmark[state]", landmark.getState()),
                new BasicNameValuePair("landmark[zip]", landmark.getZip()),
                new BasicNameValuePair("landmark[latitude]", String.valueOf(landmark.getLatitude())),
                new BasicNameValuePair("landmark[longitude]", String.valueOf(landmark.getLongitude()))
        );
        Landmark returnLandmark = (Landmark) mHttpApi.doHttpRequest(httpPost, new LandmarkParser());
        returnLandmark.setLocalId(landmark.getLocalId());
        return returnLandmark;
    }
    
    @SuppressWarnings("unchecked")
    Group<Landmark> readLandmarks() throws CogSurvException, CogSurvCredentialsException,
            CogSurvError, IOException {
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_LANDMARKS));
        return (Group<Landmark>) mHttpApi.doHttpRequest(httpGet, new GroupParser(new LandmarkParser()));
    }
    
  TravelFix createTravelFix(TravelFix travelFix) throws CogSurvException,
      CogSurvCredentialsException, CogSurvError, IOException {
    HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_TRAVEL_FIXES),
        new BasicNameValuePair("travel_fix[latitude]", String.valueOf(travelFix.getLatitude())), 
        new BasicNameValuePair("travel_fix[longitude]", String.valueOf(travelFix.getLongitude())), 
        new BasicNameValuePair("travel_fix[altitude]", String.valueOf(travelFix.getAltitude())), 
        new BasicNameValuePair("travel_fix[speed]", String.valueOf(travelFix.getSpeed())),
        new BasicNameValuePair("travel_fix[accuracy]", String.valueOf(travelFix.getAccuracy())), 
        new BasicNameValuePair("travel_fix[positioning_method]", travelFix.getPositioningMethod()), 
        new BasicNameValuePair("travel_fix[travel_mode]", travelFix.getTravelMode()), 
        new BasicNameValuePair("travel_fix[datetime]", iso8601DatetimeFormat.format(travelFix.getDatetime())));
    return (TravelFix) mHttpApi.doHttpRequest(httpPost, new TravelFixParser());
  }
    
  LandmarkVisit createLandmarkVisit(LandmarkVisit landmarkVisit) throws CogSurvException,
      CogSurvCredentialsException, CogSurvError, IOException {
    HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_LANDMARK_VISITS),
        new BasicNameValuePair("landmark_visit[landmark_id]", String.valueOf(landmarkVisit.getLandmarkId())), 
        new BasicNameValuePair("landmark_visit[datetime]", iso8601DatetimeFormat.format(landmarkVisit.getDatetime())));
    LandmarkVisit returnLandmarkVisit = (LandmarkVisit) mHttpApi.doHttpRequest(httpPost, new LandmarkVisitParser());
    returnLandmarkVisit.setLocalId(landmarkVisit.getLocalId());
    return returnLandmarkVisit;
    }
    
    DirectionDistanceEstimate createDirectionDistanceEstimate(DirectionDistanceEstimate directionDistanceEstimate) 
            throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_DIRECTION_DISTANCE_ESTIMATES),
                new BasicNameValuePair("direction_distance_estimate[landmark_visit_id]", String.valueOf(directionDistanceEstimate.getLandmarkVisitId())),
                new BasicNameValuePair("direction_distance_estimate[datetime]", iso8601DatetimeFormat.format(directionDistanceEstimate.getDatetime())),
                new BasicNameValuePair("direction_distance_estimate[direction_estimate]", String.valueOf(directionDistanceEstimate.getDirectionEstimate())),
                new BasicNameValuePair("direction_distance_estimate[distance_estimate]", String.valueOf(directionDistanceEstimate.getDistanceEstimate())),
                new BasicNameValuePair("direction_distance_estimate[distance_estimate_units]", directionDistanceEstimate.getDistanceEstimateUnits()),
                new BasicNameValuePair("direction_distance_estimate[start_landmark_id]", String.valueOf(directionDistanceEstimate.getStartLandmarkId())),
                new BasicNameValuePair("direction_distance_estimate[target_landmark_id]", String.valueOf(directionDistanceEstimate.getTargetLandmarkId()))
        );
        DirectionDistanceEstimate returnDirectionDistanceEstimate = (DirectionDistanceEstimate) mHttpApi
                .doHttpRequest(httpPost, new DirectionDistanceEstimateParser());
        returnDirectionDistanceEstimate.setLocalId(directionDistanceEstimate.getLocalId());
        return returnDirectionDistanceEstimate;
    }
    
    /*DirectionDistanceEstimate updateDirectionDistanceEstimate(DirectionDistanceEstimate directionDistanceEstimate) 
            throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
      HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_DIRECTION_DISTANCE_ESTIMATES) + '/' + directionDistanceEstimate.getServerId(),
                new BasicNameValuePair("direction_distance_estimate[landmark_visit_id]", String.valueOf(directionDistanceEstimate.getLandmarkVisitId())),
                new BasicNameValuePair("direction_distance_estimate[datetime]", String.valueOf(directionDistanceEstimate.getDatetime())),
                new BasicNameValuePair("direction_distance_estimate[direction_estimate]", String.valueOf(directionDistanceEstimate.getDirectionEstimate())),
                new BasicNameValuePair("direction_distance_estimate[distance_estimate]", String.valueOf(directionDistanceEstimate.getDistanceEstimate())),
                new BasicNameValuePair("direction_distance_estimate[distance_estimate_units]", directionDistanceEstimate.getDistanceEstimateUnits()),
                new BasicNameValuePair("direction_distance_estimate[start_landmark_id]", String.valueOf(directionDistanceEstimate.getStartLandmarkId())),
                new BasicNameValuePair("direction_distance_estimate[target_landmark_id]", String.valueOf(directionDistanceEstimate.getTargetLandmarkId()))
        );
        DirectionDistanceEstimate returnDirectionDistanceEstimate = (DirectionDistanceEstimate) mHttpApi
                .doHttpRequest(httpPost, new DirectionDistanceEstimateParser());
        returnDirectionDistanceEstimate.setLocalId(directionDistanceEstimate.getLocalId());
        return returnDirectionDistanceEstimate;
    }*/

    private String fullUrl(String url) {
        return mApiBaseUrl + url;
    }
}
