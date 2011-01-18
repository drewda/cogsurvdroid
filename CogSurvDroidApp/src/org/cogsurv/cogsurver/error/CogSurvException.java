/**
 * Code copied from foursquared project
 * Licensed under Apache License 2.0
 * 
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.cogsurver.error;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public class CogSurvException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private String mExtra;

  public CogSurvException(String message) {
    super(message);
  }

  public CogSurvException(String message, String extra) {
    super(message);
    mExtra = extra;
  }

  public String getExtra() {
    return mExtra;
  }
}
