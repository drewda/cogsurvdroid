/**
 * Code copied from foursquared project
 * Licensed under Apache License 2.0
 * 
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.droid.error;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public class CogSurvError extends CogSurvException {
    private static final long serialVersionUID = 1L;

    public CogSurvError(String message) {
        super(message);
    }

}
