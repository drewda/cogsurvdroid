package org.cogsurv.droid;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.cogsurv.droid.error.CogSurvCredentialsException;
import org.cogsurv.droid.error.CogSurvError;
import org.cogsurv.droid.error.CogSurvException;
import org.cogsurv.droid.http.AbstractHttpApi;
import org.cogsurv.droid.http.HttpApi;
import org.cogsurv.droid.http.HttpApiWithBasicAuth;
import org.cogsurv.droid.http.HttpApiWithOAuth;
import org.cogsurv.droid.parsers.CredentialsParser;
import org.cogsurv.droid.parsers.DirectionDistanceEstimateParser;
import org.cogsurv.droid.parsers.LandmarkParser;
import org.cogsurv.droid.parsers.LandmarkSetParser;
import org.cogsurv.droid.parsers.LandmarkVisitParser;
import org.cogsurv.droid.parsers.TravelFixParser;
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

/**
 * based on code by Joe LaPenna (joe@joelapenna.com)
 */
class CogSurvHttpApiV1 {
    private static final Logger LOG = Logger
            .getLogger(CogSurvHttpApiV1.class.getCanonicalName());
    private static final boolean DEBUG = CogSurv.DEBUG;

    private static final String URL_API_AUTHEXCHANGE = "/authexchange";

    private static final String URL_API_TRAVEL_FIXES = "/travel_fixes";
    private static final String URL_API_LANDMARKS = "/landmarks";
    private static final String URL_API_LANDMARK_VISITS = "/landmark_visits";
    private static final String URL_API_DIRECTION_DISTANCE_ESTIMATES = "/direction_distance_estimates";

    private final DefaultHttpClient mHttpClient = AbstractHttpApi.createHttpClient();
    private HttpApi mHttpApi;

    private final String mApiBaseUrl;
    private final AuthScope mAuthScope;

    public CogSurvHttpApiV1(String domain, String clientVersion, boolean useOAuth) {
        mApiBaseUrl = "http://" + domain + "/api/v1";
        mAuthScope = new AuthScope(domain, 80);

        if (useOAuth) {
            mHttpApi = new HttpApiWithOAuth(mHttpClient, clientVersion);
        } else {
            mHttpApi = new HttpApiWithBasicAuth(mHttpClient, clientVersion);
        }
    }

    void setCredentials(String email, String password) {
        if (email == null || email.length() == 0 || password == null || password.length() == 0) {
            if (DEBUG) LOG.log(Level.FINE, "Clearing Credentials");
            mHttpClient.getCredentialsProvider().clear();
        } else {
            if (DEBUG) LOG.log(Level.FINE, "Setting E-mail/Password: " + email + "/******");
            mHttpClient.getCredentialsProvider().setCredentials(mAuthScope,
                    new UsernamePasswordCredentials(email, password));
        }
    }

    public boolean hasCredentials() {
        return mHttpClient.getCredentialsProvider().getCredentials(mAuthScope) != null;
    }

    public void setOAuthConsumerCredentials(String oAuthConsumerKey, String oAuthConsumerSecret) {
        if (DEBUG) {
            LOG.log(Level.FINE, "Setting consumer key/secret: " + oAuthConsumerKey + " "
                    + oAuthConsumerSecret);
        }
        ((HttpApiWithOAuth) mHttpApi).setOAuthConsumerCredentials(oAuthConsumerKey,
                oAuthConsumerSecret);
    }

    public void setOAuthTokenWithSecret(String token, String secret) {
        if (DEBUG) LOG.log(Level.FINE, "Setting oauth token/secret: " + token + " " + secret);
        ((HttpApiWithOAuth) mHttpApi).setOAuthTokenWithSecret(token, secret);
    }

    public boolean hasOAuthTokenWithSecret() {
        return ((HttpApiWithOAuth) mHttpApi).hasOAuthTokenWithSecret();
    }

    /*
     * /authexchange?oauth_consumer_key=d123...a1bffb5&oauth_consumer_secret=fec...
     * 18
     */
    public Credentials authExchange(String email, String password) throws CogSurvException,
            CogSurvCredentialsException, CogSurvError, IOException {
        if (((HttpApiWithOAuth) mHttpApi).hasOAuthTokenWithSecret()) {
            throw new IllegalStateException("Cannot do authExchange with OAuthToken already set");
        }
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_AUTHEXCHANGE), //
                new BasicNameValuePair("email", email), //
                new BasicNameValuePair("password", password));
        return (Credentials) mHttpApi.doHttpRequest(httpPost, new CredentialsParser());
    }
   
    /**
     * @param localId
     * @param foursquareVenueId
     * @param name
     * @param address
     * @param city
     * @param state
     * @param zip
     * @param icon
     * @param latitude
     * @param longitude
     * @return
     * @throws CogSurvException
     * @throws CogSurvCredentialsException
     * @throws CogSurvError
     * @throws IOException
     */
    Landmark addLandmark(String localId, String foursquareVenueId, String name, String address,
            String city, String state, String zip, String icon, String latitude, String longitude)
            throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_LANDMARKS), 
                new BasicNameValuePair("foursquareVenueId", foursquareVenueId),
                new BasicNameValuePair("name", name),
                new BasicNameValuePair("address", address),
                new BasicNameValuePair("city", city),
                new BasicNameValuePair("state", state),
                new BasicNameValuePair("zip", zip),
                new BasicNameValuePair("icon", icon),
                new BasicNameValuePair("latitude", latitude),
                new BasicNameValuePair("longitude", longitude)
        );
        Landmark landmark = (Landmark) mHttpApi.doHttpRequest(httpPost, new LandmarkParser());
        landmark.setLocalId(localId);
        return landmark;
    }
    
    /**
     * 
     * @return
     * @throws CogSurvException
     * @throws CogSurvCredentialsException
     * @throws CogSurvError
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    LandmarkSet<Landmark> readLandmarks() throws CogSurvException, CogSurvCredentialsException,
            CogSurvError, IOException {
        HttpGet httpGet = mHttpApi.createHttpGet(fullUrl(URL_API_LANDMARKS));
        return (LandmarkSet<Landmark>) mHttpApi.doHttpRequest(httpGet, new LandmarkSetParser());
    }
    
    /**
     * 
     * @param latitude
     * @param longitude
     * @param altitude
     * @param speed
     * @param accuracy
     * @param positioningMethod
     * @param travelMode
     * @param datetime
     * @return
     * @throws CogSurvException
     * @throws CogSurvCredentialsException
     * @throws CogSurvError
     * @throws IOException
     */
    TravelFix createTravelFix(String latitude, String longitude, String altitude, String speed,
            String accuracy, String positioningMethod, String travelMode, Date datetime)
            throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_TRAVEL_FIXES),
                new BasicNameValuePair("latitude", latitude), new BasicNameValuePair("longitude",
                        longitude), new BasicNameValuePair("altitude", altitude),
                new BasicNameValuePair("speed", speed),
                new BasicNameValuePair("accuracy", accuracy), new BasicNameValuePair(
                        "positioning-method", positioningMethod), new BasicNameValuePair(
                        "travel-mode", travelMode), new BasicNameValuePair("datetime", String
                        .valueOf(datetime.getTime())));
        return (TravelFix) mHttpApi.doHttpRequest(httpPost, new TravelFixParser());
    }
    
    /**
     * 
     * @param localId
     * @param landmarkId
     * @return
     * @throws CogSurvException
     * @throws CogSurvCredentialsException
     * @throws CogSurvError
     * @throws IOException
     */
    LandmarkVisit createLandmarkVisit(String localId, String landmarkId) throws CogSurvException,
            CogSurvCredentialsException, CogSurvError, IOException {
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_LANDMARK_VISITS),
                new BasicNameValuePair("landmark-id", landmarkId));
        LandmarkVisit landmarkVisit = (LandmarkVisit) mHttpApi.doHttpRequest(httpPost,
                new LandmarkVisitParser());
        landmarkVisit.setLocalId(localId);
        return landmarkVisit;
    }
    
    /**
     * 
     * @param localId
     * @param landmarkVisitLocalId
     * @param landmarkVisitServerId
     * @param datetime
     * @param directionEstimate
     * @param distanceEstimate
     * @param distanceEstimateUnits
     * @param startLandmarkId
     * @param targetLandmarkId
     * @return
     * @throws CogSurvException
     * @throws CogSurvCredentialsException
     * @throws CogSurvError
     * @throws IOException
     */
    DirectionDistanceEstimate createDirectionDistanceEstimate(String localId,
            String landmarkVisitLocalId, String landmarkVisitServerId, Date datetime, String directionEstimate,
            String distanceEstimate, String distanceEstimateUnits, String startLandmarkId,
            String targetLandmarkId) throws CogSurvException, CogSurvCredentialsException,
            CogSurvError, IOException {
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_DIRECTION_DISTANCE_ESTIMATES),
                new BasicNameValuePair("landmark-visit-id", landmarkVisitServerId),
                new BasicNameValuePair("datetime", String.valueOf(datetime.getTime())),
                new BasicNameValuePair("direction-estimate", directionEstimate),
                new BasicNameValuePair("distance-estimate", distanceEstimate),
                new BasicNameValuePair("distance-estimate-units", distanceEstimateUnits),
                new BasicNameValuePair("start-landmark-id", startLandmarkId),
                new BasicNameValuePair("target-landmark-id", targetLandmarkId)
        );
        DirectionDistanceEstimate directionDistanceEstimate = (DirectionDistanceEstimate) mHttpApi
                .doHttpRequest(httpPost, new DirectionDistanceEstimateParser());
        directionDistanceEstimate.setLocalId(localId);
        directionDistanceEstimate.setLandmarkVisitLocalId(landmarkVisitLocalId);
        return directionDistanceEstimate;
    }
    
    /**
     * 
     * @param localId
     * @param serverId
     * @param landmarkVisitLocalId
     * @param landmarkVisitServerId
     * @param datetime
     * @param directionEstimate
     * @param distanceEstimate
     * @param distanceEstimateUnits
     * @param startLandmarkId
     * @param targetLandmarkId
     * @return
     * @throws CogSurvException
     * @throws CogSurvCredentialsException
     * @throws CogSurvError
     * @throws IOException
     */
    DirectionDistanceEstimate updateDirectionDistanceEstimate(String localId, String serverId,
            String landmarkVisitLocalId, String landmarkVisitServerId, Date datetime, String directionEstimate,
            String distanceEstimate, String distanceEstimateUnits, String startLandmarkId,
            String targetLandmarkId) throws CogSurvException, CogSurvCredentialsException,
            CogSurvError, IOException {
        HttpPost httpPost = mHttpApi.createHttpPost(fullUrl(URL_API_DIRECTION_DISTANCE_ESTIMATES) + '/' + serverId,
                new BasicNameValuePair("landmark-visit-id", landmarkVisitServerId),
                new BasicNameValuePair("datetime", String.valueOf(datetime.getTime())),
                new BasicNameValuePair("direction-estimate", directionEstimate),
                new BasicNameValuePair("distance-estimate", distanceEstimate),
                new BasicNameValuePair("distance-estimate-units", distanceEstimateUnits),
                new BasicNameValuePair("start-landmark-id", startLandmarkId),
                new BasicNameValuePair("target-landmark-id", targetLandmarkId)
        );
        DirectionDistanceEstimate directionDistanceEstimate = (DirectionDistanceEstimate) mHttpApi
                .doHttpRequest(httpPost, new DirectionDistanceEstimateParser());
        directionDistanceEstimate.setLocalId(localId);
        directionDistanceEstimate.setLandmarkVisitLocalId(landmarkVisitLocalId);
        return directionDistanceEstimate;
    }

    private String fullUrl(String url) {
        return mApiBaseUrl + url;
    }
}
