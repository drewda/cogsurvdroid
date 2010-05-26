/**
 * Code copied from foursquared project
 * Licensed under Apache License 2.0
 * 
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.cogsurver.http;

import org.cogsurv.cogsurver.error.CogSurvCredentialsException;
import org.cogsurv.cogsurver.error.CogSurvException;
import org.cogsurv.cogsurver.error.CogSurvParseException;
import org.cogsurv.cogsurver.parsers.Parser;
import org.cogsurv.cogsurver.types.CogSurvType;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public interface HttpApi {

    abstract public CogSurvType doHttpRequest(HttpRequestBase httpRequest,
            Parser<? extends CogSurvType> parser) throws CogSurvCredentialsException,
            CogSurvParseException, CogSurvException, IOException;

    abstract public String doHttpPost(String url, NameValuePair... nameValuePairs)
            throws CogSurvCredentialsException, CogSurvParseException, CogSurvException,
            IOException;

    abstract public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs);

    abstract public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs);
}
