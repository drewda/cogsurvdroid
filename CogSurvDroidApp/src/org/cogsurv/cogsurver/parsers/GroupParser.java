/**
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.cogsurver.parsers;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvParseException;
import org.cogsurv.cogsurver.types.CogSurvType;
import org.cogsurv.cogsurver.types.Group;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class GroupParser extends AbstractParser<Group> {
    private static final Logger LOG = Logger.getLogger(GroupParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurver.PARSER_DEBUG;

    private Parser<? extends CogSurvType> mSubParser;

    public GroupParser(Parser<? extends CogSurvType> subParser) {
        this.mSubParser = subParser;
    }

    @Override
    public Group<CogSurvType> parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            CogSurvParseException, CogSurvError {

        Group<CogSurvType> group = new Group<CogSurvType>();
        group.setType(parser.getAttributeValue(null, "type"));

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            CogSurvType item = this.mSubParser.parse(parser);
            if (DEBUG) LOG.log(Level.FINE, "adding item: " + item);
            group.add(item);
        }
        return group;
    }
}
