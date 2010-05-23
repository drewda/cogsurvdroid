
package org.cogsurv.droid;

import org.cogsurv.droid.error.CogSurvCredentialsException;
import org.cogsurv.droid.error.CogSurvError;
import org.cogsurv.droid.error.CogSurvException;
import org.cogsurv.droid.types.Credentials;
import org.cogsurv.droid.types.DirectionDistanceEstimate;
import org.cogsurv.droid.types.Landmark;
import org.cogsurv.droid.types.LandmarkSet;
import org.cogsurv.droid.types.LandmarkVisit;
import org.cogsurv.droid.types.TravelFix;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CogSurv {
    private static final Logger LOG = Logger.getLogger("org.cogsurv.droid");
    public static final boolean DEBUG = true;
    public static final boolean PARSER_DEBUG = false;

    public static final String COGSURV_API_DOMAIN = "www.cogsurv.com";

    public static final String COGSURV_MOBILE_SIGNUP = "http://www.cogsurv.com/users/sign_up";
    public static final String COGSURV_PREFERENCES = "http://foursquare.com/settings";

    private String mEmail;
    private String mPassword;
    private CogSurvHttpApiV1 mCogSurvV1;

    public CogSurv(CogSurvHttpApiV1 httpApi) {
        mCogSurvV1 = httpApi;
    }

    public void setCredentials(String email, String password) {
        mEmail = email;
        mPassword = password;
        mCogSurvV1.setCredentials(email, password);
    }

    public void setOAuthToken(String token, String secret) {
        mCogSurvV1.setOAuthTokenWithSecret(token, secret);
    }

    public void setOAuthConsumerCredentials(String oAuthConsumerKey, String oAuthConsumerSecret) {
        mCogSurvV1.setOAuthConsumerCredentials(oAuthConsumerKey, oAuthConsumerSecret);
    }

    public void clearAllCredentials() {
        setCredentials(null, null);
        setOAuthToken(null, null);
    }

    public boolean hasCredentials() {
        return mCogSurvV1.hasCredentials() && mCogSurvV1.hasOAuthTokenWithSecret();
    }

    public boolean hasLoginAndPassword() {
        return mCogSurvV1.hasCredentials();
    }

    public Credentials authExchange() throws CogSurvException, CogSurvError,
            CogSurvCredentialsException, IOException {
        if (mCogSurvV1 == null) {
            throw new NoSuchMethodError(
                    "authExchange is unavailable without a consumer key/secret.");
        }
        return mCogSurvV1.authExchange(mEmail, mPassword);
    }

    /*
     * createLandmark readLandmarks createTravelFix createLandmarkVisit
     * createDirectionDistanceEstimate updateDirectionDistanceEstimate
     */

    public Landmark addLandmark(String localId, String foursquareVenueId, String name,
            String address, String city, String state, String zip, String icon, String latitude,
            String longitude) throws CogSurvException, CogSurvCredentialsException, CogSurvError,
            IOException {
        return mCogSurvV1.addLandmark(localId, foursquareVenueId, name, address, city, state, zip,
                icon, latitude, longitude);
    }

    public LandmarkSet<Landmark> readLandmarks() throws CogSurvException,
            CogSurvCredentialsException, CogSurvError, IOException {
        return mCogSurvV1.readLandmarks();
    }

    public TravelFix createTravelFix(String latitude, String longitude, String altitude,
            String speed, String accuracy, String positioningMethod, String travelMode,
            Date datetime) throws CogSurvException, CogSurvCredentialsException, CogSurvError,
            IOException {
        return mCogSurvV1.createTravelFix(latitude, longitude, altitude, speed, accuracy,
                positioningMethod, travelMode, datetime);
    }

    public LandmarkVisit createLandmarkVisit(String localId, String landmarkId)
            throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
        return mCogSurvV1.createLandmarkVisit(localId, landmarkId);
    }

    public DirectionDistanceEstimate createDirectionDistanceEstimate(String localId,
            String landmarkVisitLocalId, String landmarkVisitServerId, Date datetime,
            String directionEstimate, String distanceEstimate, String distanceEstimateUnits,
            String startLandmarkId, String targetLandmarkId) throws CogSurvException,
            CogSurvCredentialsException, CogSurvError, IOException {
        return mCogSurvV1.createDirectionDistanceEstimate(localId, landmarkVisitLocalId,
                landmarkVisitServerId, datetime, directionEstimate, distanceEstimate,
                distanceEstimateUnits, startLandmarkId, targetLandmarkId);
    }

    public DirectionDistanceEstimate updateDirectionDistanceEstimate(String localId,
            String serverId, String landmarkVisitLocalId, String landmarkVisitServerId,
            Date datetime, String directionEstimate, String distanceEstimate,
            String distanceEstimateUnits, String startLandmarkId, String targetLandmarkId)
            throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
        return mCogSurvV1.updateDirectionDistanceEstimate(localId, serverId, landmarkVisitLocalId,
                landmarkVisitServerId, datetime, directionEstimate, distanceEstimate,
                distanceEstimateUnits, startLandmarkId, targetLandmarkId);
    }

    public static final CogSurvHttpApiV1 createHttpApi(String domain, String clientVersion,
            boolean useOAuth) {
        LOG.log(Level.INFO, "Using cogsurv.com for requests.");
        return new CogSurvHttpApiV1(domain, clientVersion, useOAuth);
    }

    public static final CogSurvHttpApiV1 createHttpApi(String clientVersion, boolean useOAuth) {
        return createHttpApi(COGSURV_API_DOMAIN, clientVersion, useOAuth);
    }
}
