/**
 * Code copied from foursquared project
 * Licensed under Apache License 2.0
 * 
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.cogsurver.parsers;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvParseException;
import org.cogsurv.cogsurver.types.Credentials;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CredentialsParser extends AbstractParser<Credentials> {
    private static final Logger LOG = Logger.getLogger(CredentialsParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurver.PARSER_DEBUG;

    @Override
    public Credentials parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            CogSurvError, CogSurvParseException {
        parser.require(XmlPullParser.START_TAG, null, null);

        Credentials credentials = new Credentials();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if ("oauth_token".equals(name)) {
                credentials.setOauthToken(parser.nextText());

            } else if ("oauth_token_secret".equals(name)) {
                credentials.setOauthTokenSecret(parser.nextText());

            } else {
                // Consume something we don't understand.
                if (DEBUG) LOG.log(Level.FINE, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return credentials;
    }
}
