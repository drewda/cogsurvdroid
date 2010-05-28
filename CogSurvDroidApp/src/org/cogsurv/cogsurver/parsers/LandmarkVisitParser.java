
package org.cogsurv.cogsurver.parsers;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvParseException;
import org.cogsurv.cogsurver.types.LandmarkVisit;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LandmarkVisitParser extends AbstractParser<LandmarkVisit> {
    private static final Logger LOG = Logger.getLogger(LandmarkVisitParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurver.PARSER_DEBUG;
    
    // http://blog.gmane.org/gmane.comp.java.joda-time.user/month=20091101
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
    
    @Override
    public LandmarkVisit parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            CogSurvError, CogSurvParseException {
        parser.require(XmlPullParser.START_TAG, null, null);

        LandmarkVisit landmarkVisit = new LandmarkVisit();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if ("id".equals(name)) {
                landmarkVisit.setServerId(Integer.parseInt(parser.nextText()));
            } else if ("landmark-id".equals(name)) {
                landmarkVisit.setLandmarkId(Integer.parseInt(parser.nextText()));
            } else if ("user-id".equals(name)) {
                landmarkVisit.setUserId(Integer.parseInt(parser.nextText()));
            } else if ("datetime".equals(name)) {
                landmarkVisit.setDatetime(formatter.parseDateTime(parser.nextText()).toDate());
            } else {
                // Consume something we don't understand.
                if (DEBUG) LOG.log(Level.FINE, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return landmarkVisit;
    }
}
