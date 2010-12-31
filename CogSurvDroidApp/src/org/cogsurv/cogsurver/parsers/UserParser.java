
package org.cogsurv.cogsurver.parsers;

import org.cogsurv.cogsurver.CogSurver;
import org.cogsurv.cogsurver.error.CogSurvError;
import org.cogsurv.cogsurver.error.CogSurvParseException;
import org.cogsurv.cogsurver.types.User;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserParser extends AbstractParser<User> {
    private static final Logger LOG = Logger.getLogger(UserParser.class.getCanonicalName());
    private static final boolean DEBUG = CogSurver.PARSER_DEBUG;

    @Override
    public User parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            CogSurvError, CogSurvParseException {
        parser.require(XmlPullParser.START_TAG, null, null);

        User user = new User();

        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String name = parser.getName();
            if ("id".equals(name)) {
              String id = parser.nextText();  
              user.setId(id);
                Log.d("CogSurv", "id: "+ id);
            } else if ("first-name".equals(name)) {
                user.setFirstName(parser.nextText());
            } else if ("last-name".equals(name)) {
                user.setLastName(parser.nextText());
            } else if ("foursquare-user-id".equals(name)) {
                user.setFoursquareUserId(parser.nextText());
            } else if ("email".equals(name)) {
                user.setEmail(parser.nextText());
            } else if ("travel-log-service-enabled".equals(name)) {
                user.setTravelLogEnabled(Boolean.parseBoolean(parser.nextText()));
            } else if ("travel-log-service-interval".equals(name)) {
                user.setTravelLogInterval(Integer.parseInt(parser.nextText()));
            } else {
                // Consume something we don't understand.
                if (DEBUG) LOG.log(Level.FINE, "Found tag that we don't recognize: " + name);
                skipSubTree(parser);
            }
        }
        return user;
    }
}
