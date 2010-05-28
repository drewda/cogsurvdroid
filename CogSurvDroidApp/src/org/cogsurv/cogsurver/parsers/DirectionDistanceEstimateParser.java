
package org.cogsurv.cogsurver.parsers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvParseException;
import org.cogsurv.cogsurver.types.DirectionDistanceEstimate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DirectionDistanceEstimateParser extends AbstractParser<DirectionDistanceEstimate> {
    private static final Logger LOG = Logger.getLogger(DirectionDistanceEstimateParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurver.PARSER_DEBUG;
    
    // http://blog.gmane.org/gmane.comp.java.joda-time.user/month=20091101
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    @Override
    public DirectionDistanceEstimate parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            CogSurvError, CogSurvParseException {
        parser.require(XmlPullParser.START_TAG, null, null);

        DirectionDistanceEstimate directionDistanceEstimate = new DirectionDistanceEstimate();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if ("id".equals(name)) {
                directionDistanceEstimate.setServerId(Integer.parseInt(parser.nextText()));
            } else if ("user-id".equals(name)) {
                directionDistanceEstimate.setUserId(Integer.parseInt(parser.nextText()));
            } else if ("landmark-visit-id".equals(name)) {
                directionDistanceEstimate.setLandmarkVisitId(Integer.parseInt(parser.nextText()));
            } else if ("datetime".equals(name)) {
                directionDistanceEstimate.setDatetime(formatter.parseDateTime(parser.nextText()).toDate());
            } else if ("direction-estimate".equals(name)) {
                directionDistanceEstimate.setDirectionEstimate(Double.parseDouble(parser.nextText()));
            } else if ("distance-estimate".equals(name)) {
                directionDistanceEstimate.setDistanceEstimate(Double.parseDouble(parser.nextText()));
            } else if ("distance-estimate-units".equals(name)) {
                directionDistanceEstimate.setDistanceEstimateUnits(parser.nextText());
            } else if ("start-landmark-id".equals(name)) {
                directionDistanceEstimate.setStartLandmarkId(Integer.parseInt(parser.nextText()));
            } else if ("target-landmark-id".equals(name)) {
                directionDistanceEstimate.setTargetLandmarkId(Integer.parseInt(parser.nextText()));
            } else {
                // Consume something we don't understand.
                if (DEBUG) LOG.log(Level.FINE, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return directionDistanceEstimate;
    }
}
