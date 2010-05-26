package org.cogsurv.cogsurver.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the URI for the regions provider and the available column names
 * and content types.
 *
 * @author Drew Dara-Abrams
 */
public interface RegionsColumns extends BaseColumns {

  public static final Uri CONTENT_URI =
      Uri.parse("content://org.cogsurv.cogsurver/regions");
  public static final String CONTENT_TYPE =
      "vnd.android.cursor.dir/vnd.cogsurver.region";
  public static final String CONTENT_ITEMTYPE =
      "vnd.android.cursor.item/vnd.cogsurver.region";
  public static final String DEFAULT_SORT_ORDER = "_id";

  /* All columns */
  public static final String SERVER_ID = "server_id";
  public static final String NAME = "name";
  public static final String USER_ID = "user_id";
}