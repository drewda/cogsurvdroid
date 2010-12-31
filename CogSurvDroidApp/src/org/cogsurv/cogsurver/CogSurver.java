package org.cogsurv.cogsurver;

import org.cogsurv.cogsurver.error.CogSurvCredentialsException;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvException;
import org.cogsurv.cogsurver.types.DirectionDistanceEstimate;
import org.cogsurv.cogsurver.types.Group;
import org.cogsurv.cogsurver.types.Landmark;
import org.cogsurv.cogsurver.types.LandmarkVisit;
import org.cogsurv.cogsurver.types.TravelFix;
import org.cogsurv.cogsurver.types.User;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CogSurver {
  private static final Logger LOG                   = Logger
                                                        .getLogger("org.cogsurv.droid");
  public static final boolean DEBUG                 = true;
  public static final boolean PARSER_DEBUG          = false;

  public static final String  COGSURV_API_DOMAIN    = "v1.cogsurv.com"; // TODO: put this in an XML config file?

  public static final String  COGSURV_MOBILE_SIGNUP = "http://www.cogsurv.com/users/sign_up";
  public static final String  COGSURV_PREFERENCES   = "http://foursquare.com/settings";

  private String              mEmail;
  private String              mPassword;
  private CogSurverHttpApiV1  mCogSurvV1;

  public CogSurver(CogSurverHttpApiV1 httpApi) {
    mCogSurvV1 = httpApi;
  }

  public void setCredentials(String email, String password) {
    mEmail = email;
    mPassword = password;
    mCogSurvV1.setCredentials(email, password);
  }

  public void clearAllCredentials() {
    setCredentials(null, null);
  }

  public boolean hasCredentials() {
    return mCogSurvV1.hasCredentials();
  }

  public boolean hasLoginAndPassword() {
    return mCogSurvV1.hasCredentials();
  }

  /*
   * createLandmark readLandmarks createTravelFix createLandmarkVisit
   * createDirectionDistanceEstimate updateDirectionDistanceEstimate
   */

  public User readUser() throws CogSurvException, CogSurvCredentialsException,
      CogSurvError, IOException {
    return mCogSurvV1.readUser();
  }
  
  public User updateUserPreferences(User user) throws CogSurvException, CogSurvCredentialsException, CogSurvError, IOException {
    return mCogSurvV1.updateUserPreferences(user);
  }

  public Landmark createLandmark(Landmark landmark) throws CogSurvException,
      CogSurvCredentialsException, CogSurvError, IOException {
    return mCogSurvV1.createLandmark(landmark);
  }

  public Group<Landmark> readLandmarks() throws CogSurvException,
      CogSurvCredentialsException, CogSurvError, IOException {
    return mCogSurvV1.readLandmarks();
  }

  public TravelFix createTravelFix(TravelFix travelFix) throws CogSurvException,
      CogSurvCredentialsException, CogSurvError, IOException {
    return mCogSurvV1.createTravelFix(travelFix);
  }

  public LandmarkVisit createLandmarkVisit(LandmarkVisit landmarkVisit)
      throws CogSurvException, CogSurvCredentialsException, CogSurvError,
      IOException {
    return mCogSurvV1.createLandmarkVisit(landmarkVisit);
  }

  public DirectionDistanceEstimate createDirectionDistanceEstimate(
      DirectionDistanceEstimate directionDistanceEstimate) throws CogSurvException,
      CogSurvCredentialsException, CogSurvError, IOException {
    return mCogSurvV1.createDirectionDistanceEstimate(directionDistanceEstimate);
  }

  /*public DirectionDistanceEstimate updateDirectionDistanceEstimate(
      DirectionDistanceEstimate directionDistanceEstimate) throws CogSurvException,
      CogSurvCredentialsException, CogSurvError, IOException {
    return mCogSurvV1.updateDirectionDistanceEstimate(directionDistanceEstimate);
  }*/

  public static final CogSurverHttpApiV1 createHttpApi(String domain,
      String clientVersion) {
    LOG.log(Level.INFO, "Using " + domain + " for requests.");
    return new CogSurverHttpApiV1(domain, clientVersion);
  }

  public static final CogSurverHttpApiV1 createHttpApi(String clientVersion) {
    return createHttpApi(COGSURV_API_DOMAIN, clientVersion);
  }

  public static class Location {
    String geolat  = null;
    String geolong = null;
    String geohacc = null;
    String geovacc = null;
    String geoalt  = null;

    public Location() {
    }

    public Location(final String geolat, final String geolong,
        final String geohacc, final String geovacc, final String geoalt) {
      this.geolat = geolat;
      this.geolong = geolong;
      this.geohacc = geohacc;
      this.geovacc = geovacc;
      this.geoalt = geovacc;
    }

    public Location(final String geolat, final String geolong) {
      this(geolat, geolong, null, null, null);
    }
  }
}
