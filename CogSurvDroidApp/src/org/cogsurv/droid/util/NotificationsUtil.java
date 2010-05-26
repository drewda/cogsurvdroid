/**
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.droid.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.cogsurv.cogsurver.error.CogSurvCredentialsException;
import org.cogsurv.cogsurver.error.CogSurvException;
import org.cogsurv.droid.CogSurvDroidSettings;
import org.cogsurv.droid.error.LocationException;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public class NotificationsUtil {
    private static final String TAG = "NotificationsUtil";
    private static final boolean DEBUG = CogSurvDroidSettings.DEBUG;

    public static void ToastReasonForFailure(Context context, Exception e) {
        if (DEBUG) Log.d(TAG, "Toasting for exception: ", e);

        if (e instanceof SocketTimeoutException) {
            Toast.makeText(context, "CogSurv server request timed out", Toast.LENGTH_SHORT).show();
            
        } else if (e instanceof SocketException) {
            Toast.makeText(context, "CogSurv server not responding", Toast.LENGTH_SHORT).show();

        } else if (e instanceof IOException) {
            Toast.makeText(context, "Network unavailable", Toast.LENGTH_SHORT).show();

        } else if (e instanceof LocationException) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

        } else if (e instanceof CogSurvCredentialsException) {
            Toast.makeText(context, "Authorization failed.", Toast.LENGTH_SHORT).show();

        } else if (e instanceof CogSurvException) {
            // CogSurvError is one of these
            String message;
            int toastLength = Toast.LENGTH_SHORT;
            if (e.getMessage() == null) {
                message = "Invalid Request";
            } else {
                message = e.getMessage();
                toastLength = Toast.LENGTH_LONG;
            }
            Toast.makeText(context, message, toastLength).show();

        } else {
            Toast.makeText(context, "A surprising new problem has occured. Try again!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
