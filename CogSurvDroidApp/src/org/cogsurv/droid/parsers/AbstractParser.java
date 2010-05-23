/**
 * Code copied from foursquared project
 * Licensed under Apache License 2.0
 * 
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.droid.parsers;

import org.cogsurv.droid.CogSurv;
import org.cogsurv.droid.error.CogSurvError;
import org.cogsurv.droid.error.CogSurvParseException;
import org.cogsurv.droid.types.CogSurvType;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public abstract class AbstractParser<T extends CogSurvType> implements Parser<T> {
    private static final Logger LOG = Logger.getLogger(AbstractParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurv.PARSER_DEBUG;

    private static XmlPullParserFactory sFactory;
    static {
        try {
            sFactory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new IllegalStateException("Could not create a factory");
        }
    }

    abstract protected T parseInner(final XmlPullParser parser) throws IOException,
            XmlPullParserException, CogSurvError, CogSurvParseException;

    /*
     * (non-Javadoc)
     * @see com.joelapenna.foursquare.parsers.Parser#parse(java.io.InputStream)
     */
    public final T parse(XmlPullParser parser) throws CogSurvParseException, CogSurvError {
        try {
            if (parser.getEventType() == XmlPullParser.START_DOCUMENT) {
                parser.nextTag();
                if (parser.getName().equals("error")) {
                    throw new CogSurvError(parser.nextText());
                }
            }
            return parseInner(parser);
        } catch (IOException e) {
            if (DEBUG) LOG.log(Level.FINE, "IOException", e);
            throw new CogSurvParseException(e.getMessage());
        } catch (XmlPullParserException e) {
            if (DEBUG) LOG.log(Level.FINE, "XmlPullParserException", e);
            throw new CogSurvParseException(e.getMessage());
        }
    }

    public static final XmlPullParser createXmlPullParser(InputStream is) {
        XmlPullParser parser;
        try {
            parser = sFactory.newPullParser();
            if (DEBUG) {
                StringBuffer sb = new StringBuffer();
                if (DEBUG) {
                    while (true) {
                        final int ch = is.read();
                        if (ch < 0) {
                            break;
                        } else {
                            sb.append((char)ch);
                        }
                    }
                    is.close();
                    LOG.log(Level.FINE, sb.toString());
                }
                parser.setInput(new StringReader(sb.toString()));
            } else {
                parser.setInput(is, null);
            }
        } catch (XmlPullParserException e) {
            throw new IllegalArgumentException();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        return parser;
    }

    public static void skipSubTree(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, null);
        int level = 1;
        while (level > 0) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.END_TAG) {
                --level;
            } else if (eventType == XmlPullParser.START_TAG) {
                ++level;
            }
        }
    }

}
