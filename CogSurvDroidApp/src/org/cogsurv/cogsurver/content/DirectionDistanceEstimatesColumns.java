package org.cogsurv.cogsurver.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the URI for the direction distance estimates provider and the available column names
 * and content types.
 *
 * @author Drew Dara-Abrams
 */
public interface DirectionDistanceEstimatesColumns extends BaseColumns {

  public static final Uri CONTENT_URI =
      Uri.parse("content://org.cogsurv.cogsurver/direction_distance_estimates");
  public static final String CONTENT_TYPE =
      "vnd.android.cursor.dir/vnd.cogsurver.direction_distance_estimate";
  public static final String CONTENT_ITEMTYPE =
      "vnd.android.cursor.item/vnd.cogsurver.direction_distance_estimate";
  public static final String DEFAULT_SORT_ORDER = "_id";

  /* All columns */
  public static final String SERVER_ID = "server_id";
  public static final String USER_ID = "user_id";
  public static final String LANDMARK_VISIT_ID = "landmark_visit_id";
  public static final String START_LANDMARK_ID = "start_landmark_id";
  public static final String TARGET_LANDMARK_ID = "target_landmark_id";
  public static final String DIRECTION_ESTIMATE = "direction_estimate";
  public static final String DISTANCE_ESTIMATE = "distance_estimate";
  public static final String DISTANCE_ESTIMATE_UNITS = "distance_estimate_units";
  public static final String TIME = "time";
}