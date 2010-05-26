package org.cogsurv.cogsurver.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the URI for the users provider and the available column names
 * and content types.
 *
 * @author Drew Dara-Abrams
 */
public interface UsersColumns extends BaseColumns {

  public static final Uri CONTENT_URI =
      Uri.parse("content://org.cogsurv.cogsurver/users");
  public static final String CONTENT_TYPE =
      "vnd.android.cursor.dir/vnd.cogsurver.user";
  public static final String CONTENT_ITEMTYPE =
      "vnd.android.cursor.item/vnd.cogsurver.user";
  public static final String DEFAULT_SORT_ORDER = "_id";

  /* All columns */
  public static final String SERVER_ID = "server_id";
  public static final String FIRST_NAME = "first_name";
  public static final String LAST_NAME = "last_name";
  public static final String EMAIL = "email";
  public static final String FOURSQUARE_USER_ID = "foursquare_user_id";
}