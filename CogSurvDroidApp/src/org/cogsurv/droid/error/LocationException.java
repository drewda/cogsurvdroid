/**
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.droid.error;

import org.cogsurv.cogsurver.error.CogSurvException;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public class LocationException extends CogSurvException {

    public LocationException() {
        super("Unable to determine your location.");
    }

    public LocationException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 1L;

}
