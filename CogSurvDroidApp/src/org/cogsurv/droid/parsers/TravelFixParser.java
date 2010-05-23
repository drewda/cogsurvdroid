
package org.cogsurv.droid.parsers;

import org.cogsurv.droid.CogSurv;
import org.cogsurv.droid.error.CogSurvError;
import org.cogsurv.droid.error.CogSurvParseException;
import org.cogsurv.droid.types.TravelFix;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TravelFixParser extends AbstractParser<TravelFix> {
    private static final Logger LOG = Logger.getLogger(TravelFixParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurv.PARSER_DEBUG;

    @Override
    public TravelFix parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            CogSurvError, CogSurvParseException {
        parser.require(XmlPullParser.START_TAG, null, null);

        TravelFix travelFix = new TravelFix();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if ("id".equals(name)) {
                travelFix.setServerId(parser.nextText());
            } else if ("user-id".equals(name)) {
                travelFix.setUserId(parser.nextText());
            } else if ("latitude".equals(name)) {
                travelFix.setLatitude(parser.nextText());
            } else if ("longitude".equals(name)) {
                travelFix.setLongitude(parser.nextText());
            } else if ("altitude".equals(name)) {
                travelFix.setAltitude(parser.nextText());
            } else if ("speed".equals(name)) {
                travelFix.setSpeed(parser.nextText());
            } else if ("accuracy".equals(name)) {
                travelFix.setAccuracy(parser.nextText());
            } else if ("positioning-method".equals(name)) {
                travelFix.setPositioningMethod(parser.nextText());
            } else if ("travel-mode".equals(name)) {
                travelFix.setTravelMode(parser.nextText());
            } else if ("datetime".equals(name)) {
                travelFix.setDatetime(new Date(parser.nextText()));
            } else {
                // Consume something we don't understand.
                if (DEBUG) LOG.log(Level.FINE, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return travelFix;
    }
}
