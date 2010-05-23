
package org.cogsurv.droid.parsers;

import org.cogsurv.droid.CogSurv;
import org.cogsurv.droid.error.CogSurvError;
import org.cogsurv.droid.error.CogSurvParseException;
import org.cogsurv.droid.types.LandmarkVisit;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LandmarkVisitParser extends AbstractParser<LandmarkVisit> {
    private static final Logger LOG = Logger.getLogger(LandmarkVisitParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurv.PARSER_DEBUG;

    @Override
    public LandmarkVisit parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            CogSurvError, CogSurvParseException {
        parser.require(XmlPullParser.START_TAG, null, null);

        LandmarkVisit landmarkVisit = new LandmarkVisit();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if ("id".equals(name)) {
                landmarkVisit.setServerId(parser.nextText());
            } else if ("landmark-id".equals(name)) {
                landmarkVisit.setLandmarkId(parser.nextText());
            } else if ("user-id".equals(name)) {
                landmarkVisit.setServerId(parser.nextText());
            } else {
                // Consume something we don't understand.
                if (DEBUG) LOG.log(Level.FINE, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return landmarkVisit;
    }
}
