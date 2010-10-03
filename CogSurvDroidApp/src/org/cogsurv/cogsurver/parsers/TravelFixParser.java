
package org.cogsurv.cogsurver.parsers;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvParseException;
import org.cogsurv.cogsurver.types.TravelFix;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TravelFixParser extends AbstractParser<TravelFix> {
    private static final Logger LOG = Logger.getLogger(TravelFixParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurver.PARSER_DEBUG;
    
    // http://blog.gmane.org/gmane.comp.java.joda-time.user/month=20091101
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    @Override
    public TravelFix parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            CogSurvError, CogSurvParseException {
        parser.require(XmlPullParser.START_TAG, null, null);

        TravelFix travelFix = new TravelFix();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if ("id".equals(name)) {
                travelFix.setServerId(Integer.parseInt(parser.nextText()));
            } else if ("user-id".equals(name)) {
                travelFix.setUserId(Integer.parseInt(parser.nextText()));
            } else if ("latitude".equals(name)) {
                travelFix.setLatitude(Double.parseDouble(parser.nextText()));
            } else if ("longitude".equals(name)) {
                travelFix.setLongitude(Double.parseDouble(parser.nextText()));
            } else if ("altitude".equals(name)) {
                travelFix.setAltitude(Double.parseDouble(parser.nextText()));
            } else if ("speed".equals(name)) {
                travelFix.setSpeed(Float.parseFloat(parser.nextText()));
            } else if ("accuracy".equals(name)) {
                travelFix.setAccuracy(Float.parseFloat(parser.nextText()));
            } else if ("positioning-method".equals(name)) {
                travelFix.setPositioningMethod(parser.nextText());
            } else if ("travel-mode".equals(name)) {
                travelFix.setTravelMode(parser.nextText());
            } else if ("datetime".equals(name)) {
                travelFix.setDatetime(formatter.parseDateTime(parser.nextText()).toDate());
            } else {
                // Consume something we don't understand.
                if (DEBUG) LOG.log(Level.FINE, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return travelFix;
    }
}
