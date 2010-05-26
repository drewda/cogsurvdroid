package org.cogsurv.cogsurver.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the URI for the landmark visits provider and the available column names
 * and content types.
 *
 * @author Drew Dara-Abrams
 */
public interface LandmarkVisitsColumns extends BaseColumns {

  public static final Uri CONTENT_URI =
      Uri.parse("content://org.cogsurv.cogsurver/landmark_visits");
  public static final String CONTENT_TYPE =
      "vnd.android.cursor.dir/vnd.cogsurver.landmark_visit";
  public static final String CONTENT_ITEMTYPE =
      "vnd.android.cursor.item/vnd.cogsurver.landmark_visit";
  public static final String DEFAULT_SORT_ORDER = "_id";

  /* All columns */
  public static final String SERVER_ID = "server_id";
  public static final String USER_ID = "user_id";
  public static final String LANDMARK_ID = "landmark_id";
  public static final String TIME = "time";
}