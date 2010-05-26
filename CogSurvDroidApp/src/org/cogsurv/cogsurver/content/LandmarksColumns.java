package org.cogsurv.cogsurver.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the URI for the users provider and the available column names
 * and content types.
 *
 * @author Drew Dara-Abrams
 */
public interface LandmarksColumns extends BaseColumns {

  public static final Uri CONTENT_URI =
      Uri.parse("content://org.cogsurv.cogsurver/landmarks");
  public static final String CONTENT_TYPE =
      "vnd.android.cursor.dir/vnd.cogsurver.landmark";
  public static final String CONTENT_ITEMTYPE =
      "vnd.android.cursor.item/vnd.cogsurver.landmark";
  public static final String DEFAULT_SORT_ORDER = "_id";

  /* All columns */
  public static final String SERVER_ID = "server_id";
  public static final String USER_ID = "user_id";
  public static final String FOURSQUARE_VENUE_ID = "foursquare_venue_id";
  public static final String LATITUDE = "latitude";
  public static final String LONGITUDE = "longitude";
  public static final String REGION_ID = "region_id";
  public static final String NAME = "name";
  public static final String ADDRESS = "address";
  public static final String CITY = "city";
  public static final String STATE = "state";
  public static final String ZIP = "zip";
  public static final String DESCRIPTION = "description";
}