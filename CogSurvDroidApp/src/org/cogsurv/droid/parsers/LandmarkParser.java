
package org.cogsurv.droid.parsers;

import org.cogsurv.droid.CogSurv;
import org.cogsurv.droid.error.CogSurvError;
import org.cogsurv.droid.error.CogSurvParseException;
import org.cogsurv.droid.types.Landmark;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LandmarkParser extends AbstractParser<Landmark> {
    private static final Logger LOG = Logger.getLogger(LandmarkParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurv.PARSER_DEBUG;

    @Override
    public Landmark parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            CogSurvError, CogSurvParseException {
        parser.require(XmlPullParser.START_TAG, null, null);

        Landmark landmark = new Landmark();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if ("address".equals(name)) {
                landmark.setAddress(parser.nextText());
            } else if ("city".equals(name)) {
                landmark.setCity(parser.nextText());
            } else if ("foursquare-venue-id".equals(name)) {
                landmark.setFoursquareVenueId(parser.nextText());
            } else if ("id".equals(name)) {
                landmark.setServerId(parser.nextText());
            } else if ("latitude".equals(name)) {
                landmark.setLatitude(parser.nextText());
            } else if ("longitude".equals(name)) {
                landmark.setLongitude(parser.nextText());
            } else if ("name".equals(name)) {
                landmark.setName(parser.nextText());
            } else if ("state".equals(name)) {
                landmark.setState(parser.nextText());
            } else if ("zip".equals(name)) {
                landmark.setZip(parser.nextText());
            } else if ("user-id".equals(name)) {
                landmark.setUserId(parser.nextText());
            } else {
                // Consume something we don't understand.
                if (DEBUG) LOG.log(Level.FINE, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return landmark;
    }
}
