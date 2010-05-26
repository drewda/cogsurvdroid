package org.cogsurv.cogsurver.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class CogSurverProvider extends ContentProvider {

  private static final String DATABASE_NAME = "cogsurver.db";
  private static final int DATABASE_VERSION = 1;
  
  private static final int USERS = 1;
  private static final int USERS_ID = 2;
  private static final String USERS_TABLE = "users";
  
  private static final int TRAVEL_FIXES = 3;
  private static final int TRAVEL_FIXES_ID = 4;
  private static final String TRAVEL_FIXES_TABLE = "travel_fixes";
  
  private static final int LANDMARKS = 5;
  private static final int LANDMARKS_ID = 6;
  private static final String LANDMARKS_TABLE = "landmarks";
  
  private static final int LANDMARK_VISITS = 7;
  private static final int LANDMARK_VISITS_ID = 8;
  private static final String LANDMARK_VISITS_TABLE = "landmark_visits";
  
  private static final int DIRECTION_DISTANCE_ESTIMATES = 9;
  private static final int DIRECTION_DISTANCE_ESTIMATES_ID = 10;
  private static final String DIRECTION_DISTANCE_ESTIMATES_TABLE = "direction_distance_estimates";
  
  private static final int REGIONS = 11;
  private static final int REGIONS_ID = 12;
  private static final String REGIONS_TABLE = "regions";
  
  public static final String TAG = "CogSurverProvider";

  /**
   * Helper which creates or upgrades the database if necessary.
   */
  private static class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE " + USERS_TABLE + " ("
          + UsersColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + UsersColumns.SERVER_ID + " INTEGER, "
          + UsersColumns.FIRST_NAME + " STRING, "
          + UsersColumns.LAST_NAME + " STRING, "
          + UsersColumns.EMAIL + " STRING, "
          + UsersColumns.FOURSQUARE_USER_ID + " STRING);");
      db.execSQL("CREATE TABLE " + TRAVEL_FIXES_TABLE + " ("
          + TravelFixesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + TravelFixesColumns.SERVER_ID + " INTEGER, "
          + TravelFixesColumns.USER_ID + " INTEGER, "
          + TravelFixesColumns.LATITUDE + " FLOAT, "
          + TravelFixesColumns.LONGITUDE + " FLOAT, "
          + TravelFixesColumns.ALTITUDE + " FLOAT, "
          + TravelFixesColumns.SPEED + " FLOAT, "
          + TravelFixesColumns.ACCURACY + " FLOAT, "
          + TravelFixesColumns.POSITIONING_METHOD + " STRING, "
          + TravelFixesColumns.TRAVEL_MODE + " STRING, "
          + TravelFixesColumns.TIME + " INTEGER);");
      db.execSQL("CREATE TABLE " + LANDMARKS_TABLE + " ("
          + LandmarksColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + LandmarksColumns.USER_ID + " INTEGER, "
          + LandmarksColumns.SERVER_ID + " INTEGER, "
          + LandmarksColumns.FOURSQUARE_VENUE_ID + " STRING, "
          + LandmarksColumns.LATITUDE + " FLOAT, "
          + LandmarksColumns.LONGITUDE + " FLOAT, "
          + LandmarksColumns.NAME + " STRING, "
          + LandmarksColumns.REGION_ID + " INTEGER, "
          + LandmarksColumns.ADDRESS + " STRING, "
          + LandmarksColumns.CITY + " STRING, "
          + LandmarksColumns.STATE + " STRING, "
          + LandmarksColumns.ZIP + " STRING, "
          + LandmarksColumns.DESCRIPTION + " TEXT);");
      db.execSQL("CREATE TABLE " + LANDMARK_VISITS_TABLE + " ("
          + LandmarkVisitsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + LandmarkVisitsColumns.USER_ID + " INTEGER, "
          + LandmarkVisitsColumns.SERVER_ID + " INTEGER, "
          + LandmarkVisitsColumns.LANDMARK_ID + " INTEGER, "
          + LandmarkVisitsColumns.TIME + " INTEGER);");
      db.execSQL("CREATE TABLE " + DIRECTION_DISTANCE_ESTIMATES_TABLE + " ("
          + DirectionDistanceEstimatesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + DirectionDistanceEstimatesColumns.SERVER_ID + " INTEGER, "
          + DirectionDistanceEstimatesColumns.USER_ID + " INTEGER, "
          + DirectionDistanceEstimatesColumns.LANDMARK_VISIT_ID + " INTEGER, "
          + DirectionDistanceEstimatesColumns.START_LANDMARK_ID + " INTEGER, "
          + DirectionDistanceEstimatesColumns.TARGET_LANDMARK_ID + " INTEGER, "
          + DirectionDistanceEstimatesColumns.DIRECTION_ESTIMATE + " FLOAT, "
          + DirectionDistanceEstimatesColumns.DISTANCE_ESTIMATE + " FLOAT, "
          + DirectionDistanceEstimatesColumns.DISTANCE_ESTIMATE_UNITS + " STRING, "
          + DirectionDistanceEstimatesColumns.TIME + " INTEGER);");
      db.execSQL("CREATE TABLE " + REGIONS_TABLE + " ("
          + RegionsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
          + RegionsColumns.SERVER_ID + " INTEGER, "
          + RegionsColumns.NAME + " STRING, "
          + RegionsColumns.USER_ID + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
          + newVersion + ", which will destroy all old data");
      db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
      db.execSQL("DROP TABLE IF EXISTS " + TRAVEL_FIXES_TABLE);
      db.execSQL("DROP TABLE IF EXISTS " + LANDMARKS_TABLE);
      db.execSQL("DROP TABLE IF EXISTS " + LANDMARK_VISITS_TABLE);
      db.execSQL("DROP TABLE IF EXISTS " + DIRECTION_DISTANCE_ESTIMATES_TABLE);
      db.execSQL("DROP TABLE IF EXISTS " + REGIONS_TABLE);
      onCreate(db);
    }
  }

  private final UriMatcher urlMatcher;

  private SQLiteDatabase db;

  public CogSurverProvider() {
    urlMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "users", USERS);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "users/#", USERS_ID);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "travel_fixes", TRAVEL_FIXES);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "travel_fixes/#", TRAVEL_FIXES_ID);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "landmarks", LANDMARKS);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "landmarks/#", LANDMARKS_ID);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "landmark_visits", LANDMARK_VISITS);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "landmarks_visits/#", LANDMARK_VISITS_ID);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "direction_distance_estimates", DIRECTION_DISTANCE_ESTIMATES);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "direction_distance_estimates/#", DIRECTION_DISTANCE_ESTIMATES_ID);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "regions", REGIONS);
    urlMatcher.addURI(CogSurverProviderUtils.AUTHORITY, "regions/#", REGIONS_ID);
  }

  @Override
  public boolean onCreate() {
    DatabaseHelper dbHelper = new DatabaseHelper(getContext());
    db = dbHelper.getWritableDatabase();
   return db != null;
  }

  @Override
  public int delete(Uri url, String where, String[] selectionArgs) {
    if (urlMatcher.match(url) == USERS) {
      Log.w(CogSurverProvider.TAG, "provider users delete!");
      int count = db.delete(USERS_TABLE, where, selectionArgs);
      getContext().getContentResolver().notifyChange(url, null, true);
      return count;
    } else if (urlMatcher.match(url) == TRAVEL_FIXES) {
      Log.w(CogSurverProvider.TAG, "provider travel fixes delete!");
      int count = db.delete(TRAVEL_FIXES_TABLE, where, selectionArgs);
      getContext().getContentResolver().notifyChange(url, null, true);
      return count;
    } else if (urlMatcher.match(url) == LANDMARKS) {
      Log.w(CogSurverProvider.TAG, "provider landmarks delete!");
      int count = db.delete(LANDMARKS_TABLE, where, selectionArgs);
      getContext().getContentResolver().notifyChange(url, null, true);
      return count;
    } else if (urlMatcher.match(url) == LANDMARK_VISITS) {
      Log.w(CogSurverProvider.TAG, "provider landmark visits delete!");
      int count = db.delete(LANDMARK_VISITS_TABLE, where, selectionArgs);
      getContext().getContentResolver().notifyChange(url, null, true);
      return count;
    } else if (urlMatcher.match(url) == DIRECTION_DISTANCE_ESTIMATES) {
      Log.w(CogSurverProvider.TAG, "provider direction distance estimates delete!");
      int count = db.delete(DIRECTION_DISTANCE_ESTIMATES_TABLE, where, selectionArgs);
      getContext().getContentResolver().notifyChange(url, null, true);
      return count;
    } else if (urlMatcher.match(url) == REGIONS) {
      Log.w(CogSurverProvider.TAG, "provider regions delete!");
      int count = db.delete(REGIONS_TABLE, where, selectionArgs);
      getContext().getContentResolver().notifyChange(url, null, true);
      return count;
    } else {
      throw new IllegalArgumentException("Unknown URL " + url);
    }
  }

  @Override
  public String getType(Uri url) {
    switch (urlMatcher.match(url)) {
      case USERS:
        return UsersColumns.CONTENT_TYPE;
      case USERS_ID:
        return UsersColumns.CONTENT_ITEMTYPE;
      case TRAVEL_FIXES:
        return TravelFixesColumns.CONTENT_TYPE;
      case TRAVEL_FIXES_ID:
        return TravelFixesColumns.CONTENT_ITEMTYPE;
      case LANDMARKS:
        return LandmarksColumns.CONTENT_TYPE;
      case LANDMARKS_ID:
        return LandmarksColumns.CONTENT_ITEMTYPE;
      case LANDMARK_VISITS:
        return LandmarkVisitsColumns.CONTENT_TYPE;
      case LANDMARK_VISITS_ID:
        return LandmarkVisitsColumns.CONTENT_ITEMTYPE;
      case DIRECTION_DISTANCE_ESTIMATES:
        return DirectionDistanceEstimatesColumns.CONTENT_TYPE;
      case DIRECTION_DISTANCE_ESTIMATES_ID:
        return DirectionDistanceEstimatesColumns.CONTENT_ITEMTYPE;
      case REGIONS:
        return RegionsColumns.CONTENT_TYPE;
      case REGIONS_ID:
        return RegionsColumns.CONTENT_ITEMTYPE;
      default:
        throw new IllegalArgumentException("Unknown URL " + url);
    }
  }

  @Override
  public Uri insert(Uri url, ContentValues initialValues) {
    Log.d(CogSurverProvider.TAG, "CogSurverProvider.insert");
    ContentValues values;
    if (initialValues != null) {
      values = initialValues;
    } else {
      values = new ContentValues();
    }
    if (urlMatcher.match(url) == USERS) {
      return insertUser(url, values);
    } else if (urlMatcher.match(url) == TRAVEL_FIXES) {
      return insertTravelFix(url, values);
    } else if (urlMatcher.match(url) == LANDMARKS) {
      return insertLandmark(url, values);
    } else if (urlMatcher.match(url) == LANDMARK_VISITS) {
      return insertLandmarkVisit(url, values);
    } else if (urlMatcher.match(url) == DIRECTION_DISTANCE_ESTIMATES) {
      return insertDirectionDistanceEstimate(url, values);
    } else if (urlMatcher.match(url) == REGIONS) {
      return insertRegion(url, values);
    } else {
      throw new IllegalArgumentException("Unknown URL " + url);
    }
  }

  private Uri insertUser(Uri url, ContentValues values) {
    Log.d(CogSurverProvider.TAG, "CogSurverProvider.insertUser: " + values.toString());
    values.remove(UsersColumns._ID); // we don't want to specify the localId
    boolean hasServerId = values.containsKey(UsersColumns.SERVER_ID);
    boolean hasFirstName = values.containsKey(UsersColumns.FIRST_NAME);
    boolean hasLastName = values.containsKey(UsersColumns.LAST_NAME);
    boolean hasEmail = values.containsKey(UsersColumns.EMAIL);
    // UsersColumns.FOURSQUARE_USER_ID is optional
    if (!hasServerId || !hasFirstName || !hasLastName || !hasEmail) {
      throw new IllegalArgumentException(
          "serverId, firstName, lastName, and email fields are required.");
    }
    long rowId = db.insert(USERS_TABLE, UsersColumns._ID, values);
    if (rowId >= 0) {
      Uri uri = ContentUris.appendId(
          UsersColumns.CONTENT_URI.buildUpon(), rowId).build();
      getContext().getContentResolver().notifyChange(url, null, true);
      return uri;
    }
    throw new SQLiteException("Failed to insert row into " + url);
  }

  private Uri insertTravelFix(Uri url, ContentValues values) {
    Log.d(CogSurverProvider.TAG, "CogSurverProvider.insertTravelFix: " + values.toString());
    values.remove(TravelFixesColumns._ID); // we don't want to specify the localId
    boolean hasUserId = values.containsKey(TravelFixesColumns.USER_ID);
    boolean hasLatitude = values.containsKey(TravelFixesColumns.LATITUDE);
    boolean hasLongitude = values.containsKey(TravelFixesColumns.LONGITUDE);
    boolean hasAltitude = values.containsKey(TravelFixesColumns.ALTITUDE);
    boolean hasSpeed = values.containsKey(TravelFixesColumns.SPEED);
    boolean hasAccuracy = values.containsKey(TravelFixesColumns.ACCURACY);
    boolean hasPositioningMethod = values.containsKey(TravelFixesColumns.POSITIONING_METHOD);
    boolean hasTime = values.containsKey(TravelFixesColumns.TIME);
    if (!hasUserId || !hasLatitude || !hasLongitude || !hasAltitude || !hasSpeed || !hasAccuracy || !hasPositioningMethod || !hasTime) {
      throw new IllegalArgumentException(
          "Required fields: userId, latitude, longitude, altitude, speed, accuracy, positioningMethod, time.");
    }
    long rowId = db.insert(TRAVEL_FIXES_TABLE, TravelFixesColumns._ID, values);
    if (rowId > 0) {
      Uri uri = ContentUris.appendId(
          TravelFixesColumns.CONTENT_URI.buildUpon(), rowId).build();
      getContext().getContentResolver().notifyChange(url, null, true);
      return uri;
    }
    throw new SQLException("Failed to insert row into " + url);
  }
  
  private Uri insertLandmark(Uri url, ContentValues values) {
    Log.d(CogSurverProvider.TAG, "CogSurverProvider.insertLandmark: " + values.toString());
    values.remove(LandmarksColumns._ID); // we don't want to specify the localId
    boolean hasUserId = values.containsKey(LandmarksColumns.USER_ID);
    boolean hasLatitude = values.containsKey(LandmarksColumns.LATITUDE);
    boolean hasLongitude = values.containsKey(LandmarksColumns.LONGITUDE);
    boolean hasName = values.containsKey(LandmarksColumns.NAME);
    if (!hasUserId || !hasLatitude || !hasLongitude || !hasName) {
      throw new IllegalArgumentException(
          "Required fields: userId, latitude, longitude, altitude, speed, accuracy, positioningMethod, time.");
    }
    long rowId = db.insert(LANDMARKS_TABLE, LandmarksColumns._ID, values);
    Log.d("CogSurv", "going to insert values: " + values.toString());
    if (rowId > 0) {
      Uri uri = ContentUris.appendId(
          LandmarksColumns.CONTENT_URI.buildUpon(), rowId).build();
      getContext().getContentResolver().notifyChange(url, null, true);
      return uri;
    }
    throw new SQLException("Failed to insert row into " + url);
  }
  
  private Uri insertLandmarkVisit(Uri url, ContentValues values) {
    Log.d(CogSurverProvider.TAG, "CogSurverProvider.insertLandmarkVisit: " + values.toString());
    values.remove(LandmarkVisitsColumns._ID); // we don't want to specify the localId
    boolean hasUserId = values.containsKey(LandmarkVisitsColumns.USER_ID);
    boolean hasLandmarkId = values.containsKey(LandmarkVisitsColumns.LANDMARK_ID);
    boolean hasTime = values.containsKey(LandmarkVisitsColumns.TIME);
    if (!hasUserId || !hasLandmarkId || !hasTime) {
      throw new IllegalArgumentException(
          "Required fields: userId, landmarkId, time.");
    }
    long rowId = db.insert(LANDMARK_VISITS_TABLE, LandmarkVisitsColumns._ID, values);
    if (rowId > 0) {
      Uri uri = ContentUris.appendId(
          LandmarkVisitsColumns.CONTENT_URI.buildUpon(), rowId).build();
      getContext().getContentResolver().notifyChange(url, null, true);
      return uri;
    }
    throw new SQLException("Failed to insert row into " + url);
  }
  
  private Uri insertDirectionDistanceEstimate(Uri url, ContentValues values) {
    Log.d(CogSurverProvider.TAG, "CogSurverProvider.insertDirectionDistanceEstimate: " + values.toString());
    values.remove(DirectionDistanceEstimatesColumns._ID); // we don't want to specify the localId
    boolean hasUserId = values.containsKey(DirectionDistanceEstimatesColumns.USER_ID);
    boolean hasLandmarkVisitId = values.containsKey(DirectionDistanceEstimatesColumns.LANDMARK_VISIT_ID);
    boolean hasStartLandmarkId = values.containsKey(DirectionDistanceEstimatesColumns.START_LANDMARK_ID);
    boolean hasTargetLandmarkId = values.containsKey(DirectionDistanceEstimatesColumns.TARGET_LANDMARK_ID);
    boolean hasDirectionEstimate = values.containsKey(DirectionDistanceEstimatesColumns.DIRECTION_ESTIMATE);
    boolean hasDistanceEstimate = values.containsKey(DirectionDistanceEstimatesColumns.DISTANCE_ESTIMATE);
    boolean hasDistanceEstimateUnits = values.containsKey(DirectionDistanceEstimatesColumns.DISTANCE_ESTIMATE_UNITS);
    boolean hasTime = values.containsKey(TravelFixesColumns.TIME);
    if (!hasUserId || !hasLandmarkVisitId || !hasStartLandmarkId || !hasTargetLandmarkId || !hasDirectionEstimate || !hasDistanceEstimate || !hasDistanceEstimateUnits || !hasTime) {
      throw new IllegalArgumentException(
          "Required fields: userId, landmarkVisitId, startLandmarkId, targetLandmarkId, directionEstimate, distanceEstimate, distanceEstimateUnits, time.");
    }
    long rowId = db.insert(TRAVEL_FIXES_TABLE, TravelFixesColumns._ID, values);
    if (rowId > 0) {
      Uri uri = ContentUris.appendId(
          TravelFixesColumns.CONTENT_URI.buildUpon(), rowId).build();
      getContext().getContentResolver().notifyChange(url, null, true);
      return uri;
    }
    throw new SQLException("Failed to insert row into " + url);
  }
  
  private Uri insertRegion(Uri url, ContentValues values) {
    Log.d(CogSurverProvider.TAG, "CogSurverProvider.insertRegion: " + values.toString());
    values.remove(RegionsColumns._ID); // we don't want to specify the localId
    boolean hasUserId = values.containsKey(RegionsColumns.USER_ID);
    boolean hasName = values.containsKey(RegionsColumns.NAME);
    if (!hasUserId || !hasName) {
      throw new IllegalArgumentException(
          "Required fields: userId, name.");
    }
    long rowId = db.insert(REGIONS_TABLE, RegionsColumns._ID, values);
    if (rowId > 0) {
      Uri uri = ContentUris.appendId(
          RegionsColumns.CONTENT_URI.buildUpon(), rowId).build();
      getContext().getContentResolver().notifyChange(url, null, true);
      return uri;
    }
    throw new SQLException("Failed to insert row into " + url);
  }

  @Override
  public Cursor query(
      Uri url, String[] projection, String selection, String[] selectionArgs,
      String sort) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    int match = urlMatcher.match(url);
    String sortOrder = null;
    if (match == USERS) {
      qb.setTables(USERS_TABLE);
      if (sort != null) {
        sortOrder = sort;
      } else {
        sortOrder = UsersColumns.DEFAULT_SORT_ORDER;
      }
    } else if (match == USERS_ID) {
      qb.setTables(USERS_TABLE);
      qb.appendWhere("_id=" + url.getPathSegments().get(1));
    } else if (match == TRAVEL_FIXES) {
      qb.setTables(TRAVEL_FIXES_TABLE);
      if (sort != null) {
        sortOrder = sort;
      } else {
        sortOrder = TravelFixesColumns.DEFAULT_SORT_ORDER;
      }
    } else if (match == TRAVEL_FIXES_ID) {
      qb.setTables(TRAVEL_FIXES_TABLE);
      qb.appendWhere("_id=" + url.getPathSegments().get(1));
    } else if (match == LANDMARKS) {
      qb.setTables(TRAVEL_FIXES_TABLE);
      if (sort != null) {
        sortOrder = sort;
      } else {
        sortOrder = LandmarksColumns.DEFAULT_SORT_ORDER;
      }
    } else if (match == LANDMARKS_ID) {
      qb.setTables(LANDMARKS_TABLE);
      qb.appendWhere("_id=" + url.getPathSegments().get(1));
    } else if (match == LANDMARK_VISITS) {
      qb.setTables(LANDMARK_VISITS_TABLE);
      if (sort != null) {
        sortOrder = sort;
      } else {
        sortOrder = LandmarkVisitsColumns.DEFAULT_SORT_ORDER;
      }
    } else if (match == LANDMARK_VISITS_ID) {
      qb.setTables(LANDMARK_VISITS_TABLE);
      qb.appendWhere("_id=" + url.getPathSegments().get(1));
    } else if (match == DIRECTION_DISTANCE_ESTIMATES) {
      qb.setTables(DIRECTION_DISTANCE_ESTIMATES_TABLE);
      if (sort != null) {
        sortOrder = sort;
      } else {
        sortOrder = DirectionDistanceEstimatesColumns.DEFAULT_SORT_ORDER;
      }
    } else if (match == DIRECTION_DISTANCE_ESTIMATES_ID) {
      qb.setTables(DIRECTION_DISTANCE_ESTIMATES_TABLE);
      qb.appendWhere("_id=" + url.getPathSegments().get(1));
    } else if (match == REGIONS) {
      qb.setTables(REGIONS_TABLE);
      if (sort != null) {
        sortOrder = sort;
      } else {
        sortOrder = RegionsColumns.DEFAULT_SORT_ORDER;
      }
    } else if (match == REGIONS_ID) {
      qb.setTables(REGIONS_TABLE);
      qb.appendWhere("_id=" + url.getPathSegments().get(1));
    } else {
      throw new IllegalArgumentException("Unknown URL " + url);
    }

    Cursor c = qb.query(db, projection, selection, selectionArgs, null, null,
        sortOrder);
    c.setNotificationUri(getContext().getContentResolver(), url);
    return c;
  }

  @Override
  public int update(Uri url, ContentValues values, String where,
      String[] selectionArgs) {
    int count;
    int match = urlMatcher.match(url);
    if (match == USERS) {
      count = db.update(USERS_TABLE, values, where, selectionArgs);
    } else if (match == USERS_ID) {
      String segment = url.getPathSegments().get(1);
      count = db.update(USERS_TABLE, values, "_id=" + segment
          + (!TextUtils.isEmpty(where)
              ? " AND (" + where + ')'
              : ""),
          selectionArgs);
    } else if (match == TRAVEL_FIXES) {
      count = db.update(TRAVEL_FIXES_TABLE, values, where, selectionArgs);
    } else if (match == TRAVEL_FIXES_ID) {
      String segment = url.getPathSegments().get(1);
      count = db.update(TRAVEL_FIXES_TABLE, values, "_id=" + segment
          + (!TextUtils.isEmpty(where)
              ? " AND (" + where + ')'
              : ""),
          selectionArgs);
    } else if (match == LANDMARKS) {
      count = db.update(LANDMARKS_TABLE, values, where, selectionArgs);
    } else if (match == LANDMARKS_ID) {
      String segment = url.getPathSegments().get(1);
      count = db.update(LANDMARKS_TABLE, values, "_id=" + segment
          + (!TextUtils.isEmpty(where)
              ? " AND (" + where + ')'
              : ""),
          selectionArgs);
    } else if (match == LANDMARK_VISITS) {
      count = db.update(LANDMARK_VISITS_TABLE, values, where, selectionArgs);
    } else if (match == LANDMARK_VISITS_ID) {
      String segment = url.getPathSegments().get(1);
      count = db.update(LANDMARK_VISITS_TABLE, values, "_id=" + segment
          + (!TextUtils.isEmpty(where)
              ? " AND (" + where + ')'
              : ""),
          selectionArgs);
    } else if (match == DIRECTION_DISTANCE_ESTIMATES) {
      count = db.update(DIRECTION_DISTANCE_ESTIMATES_TABLE, values, where, selectionArgs);
    } else if (match == DIRECTION_DISTANCE_ESTIMATES_ID) {
      String segment = url.getPathSegments().get(1);
      count = db.update(DIRECTION_DISTANCE_ESTIMATES_TABLE, values, "_id=" + segment
          + (!TextUtils.isEmpty(where)
              ? " AND (" + where + ')'
              : ""),
          selectionArgs);
    } else if (match == REGIONS) {
      count = db.update(REGIONS_TABLE, values, where, selectionArgs);
    } else if (match == REGIONS_ID) {
      String segment = url.getPathSegments().get(1);
      count = db.update(REGIONS_TABLE, values, "_id=" + segment
          + (!TextUtils.isEmpty(where)
              ? " AND (" + where + ')'
              : ""),
          selectionArgs);
    } else {
      throw new IllegalArgumentException("Unknown URL " + url);
    }
    getContext().getContentResolver().notifyChange(url, null, true);
    return count;
  }

}
