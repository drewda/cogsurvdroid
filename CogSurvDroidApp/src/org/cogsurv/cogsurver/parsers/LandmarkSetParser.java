package org.cogsurv.cogsurver.parsers;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvParseException;
import org.cogsurv.cogsurver.types.Landmark;
import org.cogsurv.cogsurver.types.LandmarkSet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class LandmarkSetParser extends AbstractParser<LandmarkSet> {
    private static final Logger LOG = Logger.getLogger(LandmarkSetParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurver.PARSER_DEBUG;

    private LandmarkParser mSubParser;

    public LandmarkSetParser() {
        this.mSubParser = new LandmarkParser();
    }

    @Override
    public LandmarkSet<Landmark> parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
    CogSurvParseException, CogSurvError {

        LandmarkSet<Landmark> landmarkSet = new LandmarkSet<Landmark>();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            Landmark landmark = this.mSubParser.parse(parser);
            if (DEBUG) LOG.log(Level.FINE, "adding landmark: " + landmark);
            landmarkSet.add(landmark);
        }
        return landmarkSet;
    }
}
