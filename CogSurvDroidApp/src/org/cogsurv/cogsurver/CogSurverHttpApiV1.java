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
                new BasicNameValuePair("foursquareVenueId", landmark.getFoursquareVenueId()),
                new BasicNameValuePair("name", landmark.getName()),
                new BasicNameValuePair("address", landmark.getAddress()),
                new BasicNameValuePair("city", landmark.getCity()),
                new BasicNameValuePair("state", landmark.getState()),
                new BasicNameValuePair("zip", landmark.getZip()),
                new BasicNameValuePair("latitude", String.valueOf(landmark.getLatitude())),
                new BasicNameValuePair("longitude", String.valueOf(landmark.getLongitude()))
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
        new BasicNameValuePair("travel_fix[travel-mode]", travelFix.getTravelMode()), 
        new BasicNameValuePair("travel_fix[datetime]", String.valueOf(travelFix.getDatetime().getTime())));
    return (TravelFix) mHttpApi.doHttpRequest(httpPost, new TravelFixParser());
  }
    
    LandmarkVisit createLandmarkVisit(LandmarkVisit landmarkVisit) throws CogSurvException,
            CogSurvCredentialsException, CogSurvError, IOException {
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_LANDMARK_VISITS),
                new BasicNameValuePair("landmark-id", String.valueOf(landmarkVisit.getLandmarkId())));
        LandmarkVisit returnLandmarkVisit = (LandmarkVisit) mHttpApi.doHttpRequest(httpPost,
                new LandmarkVisitParser());
        returnLandmarkVisit.setLocalId(landmarkVisit.getLocalId());
        return returnLandmarkVisit;
    }
    
    DirectionDistanceEstimate createDirectionDistanceEstimate(DirectionDistanceEstimate directionDistanceEstimate) 
            throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_DIRECTION_DISTANCE_ESTIMATES),
                new BasicNameValuePair("landmark-visit-id", String.valueOf(directionDistanceEstimate.getLandmarkVisitId())),
                new BasicNameValuePair("datetime", String.valueOf(directionDistanceEstimate.getDatetime().getTime())),
                new BasicNameValuePair("direction-estimate", String.valueOf(directionDistanceEstimate.getDirectionEstimate())),
                new BasicNameValuePair("distance-estimate", String.valueOf(directionDistanceEstimate.getDistanceEstimate())),
                new BasicNameValuePair("distance-estimate-units", directionDistanceEstimate.getDistanceEstimateUnits()),
                new BasicNameValuePair("start-landmark-id", String.valueOf(directionDistanceEstimate.getStartLandmarkId())),
                new BasicNameValuePair("target-landmark-id", String.valueOf(directionDistanceEstimate.getTargetLandmarkId()))
        );
        DirectionDistanceEstimate returnDirectionDistanceEstimate = (DirectionDistanceEstimate) mHttpApi
                .doHttpRequest(httpPost, new DirectionDistanceEstimateParser());
        returnDirectionDistanceEstimate.setLocalId(directionDistanceEstimate.getLocalId());
        return returnDirectionDistanceEstimate;
    }
    
    DirectionDistanceEstimate updateDirectionDistanceEstimate(DirectionDistanceEstimate directionDistanceEstimate) 
            throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
      HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_DIRECTION_DISTANCE_ESTIMATES) + '/' + directionDistanceEstimate.getServerId(),
                new BasicNameValuePair("landmark-visit-id", String.valueOf(directionDistanceEstimate.getLandmarkVisitId())),
                new BasicNameValuePair("datetime", String.valueOf(directionDistanceEstimate.getDatetime().getTime())),
                new BasicNameValuePair("direction-estimate", String.valueOf(directionDistanceEstimate.getDirectionEstimate())),
                new BasicNameValuePair("distance-estimate", String.valueOf(directionDistanceEstimate.getDistanceEstimate())),
                new BasicNameValuePair("distance-estimate-units", directionDistanceEstimate.getDistanceEstimateUnits()),
                new BasicNameValuePair("start-landmark-id", String.valueOf(directionDistanceEstimate.getStartLandmarkId())),
                new BasicNameValuePair("target-landmark-id", String.valueOf(directionDistanceEstimate.getTargetLandmarkId()))
        );
        DirectionDistanceEstimate returnDirectionDistanceEstimate = (DirectionDistanceEstimate) mHttpApi
                .doHttpRequest(httpPost, new DirectionDistanceEstimateParser());
        returnDirectionDistanceEstimate.setLocalId(directionDistanceEstimate.getLocalId());
        return returnDirectionDistanceEstimate;
    }

    private String fullUrl(String url) {
        return mApiBaseUrl + url;
    }
}
