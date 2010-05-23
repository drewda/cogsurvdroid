package org.cogsurv.droid.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

import com.joelapenna.foursquare.types.Checkin;
import com.joelapenna.foursquare.types.Group;
import com.joelapenna.foursquare.types.Venue;

public class SurveyingTargetSet extends ArrayList<SurveyingTarget> {
    private static final long serialVersionUID = 1L;
    
    public SurveyingTargetSet(LandmarkSet<Checkin> history) {
        Checkin checkin;
        SurveyingTarget surveyingTarget;
        ListIterator<Checkin> iterator = history.listIterator();
        while (iterator.hasNext()) {
            checkin = iterator.next();
            surveyingTarget = this.containsVenue(checkin.getVenue());
            if (surveyingTarget != null) {
                surveyingTarget.setRecentVisits(surveyingTarget.getRecentVisits() + 1);
            }
            else {
                surveyingTarget = new SurveyingTarget();
                surveyingTarget.setVenue(checkin.getVenue());
                surveyingTarget.setRecentVisits(1);
                this.add(surveyingTarget);
            } 
        }
        Collections.sort(this); // order by recentVisits
    }
    
    public SurveyingTarget containsVenue(Venue venue) {
        SurveyingTarget surveyingTarget;
        ListIterator<SurveyingTarget> iterator = this.listIterator();
        while (iterator.hasNext()) {
            surveyingTarget = iterator.next();
            if (surveyingTarget.getVenue().getId().equals(venue.getId())) {
                return surveyingTarget;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param venue the Venue to remove
     * @return true/false
     * 
     * used to remove the start landmark from the set of possible target landmarks
     */
    public Boolean removeVenue(Venue venue) {
        SurveyingTarget surveyingTarget;
        ListIterator<SurveyingTarget> iterator = this.listIterator();
        while (iterator.hasNext()) {
            surveyingTarget = iterator.next();
            if (surveyingTarget.getVenue().getId().equals(venue.getId())) {
                return this.remove(surveyingTarget);
            }
        }
        return false;
    }
}
