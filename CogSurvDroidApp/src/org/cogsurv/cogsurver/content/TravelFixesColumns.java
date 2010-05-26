package org.cogsurv.cogsurver.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the URI for the travel fixes provider and the available column names
 * and content types.
 *
 * @author Drew Dara-Abrams
 */
public interface TravelFixesColumns extends BaseColumns {

  public static final Uri CONTENT_URI =
      Uri.parse("content://org.cogsurv.cogsurver/travel_fixes");
  public static final String CONTENT_TYPE =
      "vnd.android.cursor.dir/vnd.cogsurver.travel_fix";
  public static final String CONTENT_ITEMTYPE =
      "vnd.android.cursor.item/vnd.cogsurver.travel_fix";
  public static final String DEFAULT_SORT_ORDER = "_id";

  /* All columns */
  public static final String SERVER_ID = "server_id";
  public static final String USER_ID = "user_id";
  public static final String LATITUDE = "latitude";
  public static final String LONGITUDE = "longitude";
  public static final String ALTITUDE = "altitude";
  public static final String SPEED = "speed";
  public static final String ACCURACY = "accuracy";
  public static final String POSITIONING_METHOD = "positioning_method";
  public static final String TRAVEL_MODE = "travel_mode";
  public static final String TIME = "time";
}