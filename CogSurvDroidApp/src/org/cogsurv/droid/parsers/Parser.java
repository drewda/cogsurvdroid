/**
 * Code copied from foursquared project
 * Licensed under Apache License 2.0
 * 
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.droid.parsers;

import org.cogsurv.droid.error.CogSurvError;
import org.cogsurv.droid.error.CogSurvParseException;
import org.cogsurv.droid.types.CogSurvType;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 * @param <T>
 */
public interface Parser<T extends CogSurvType> {

    public abstract T parse(XmlPullParser parser) throws CogSurvError, CogSurvParseException;

}
