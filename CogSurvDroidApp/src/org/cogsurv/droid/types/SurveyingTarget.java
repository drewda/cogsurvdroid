package org.cogsurv.droid.types;

/***
 * @author Drew Dara-Abrams
 */

import java.util.Date;

public class SurveyingTarget implements CogSurvType, Comparable<SurveyingTarget> {
    private Landmark mLandmark;
    private Integer mRecentVisits;
    private Boolean mAsked;
    private Boolean mSkipped;
    private Float mDirectionEstimate;
    private Double mDistanceEstimate;
    private String mDistanceEstimateUnits;
    private Date mEstimateDate;
    private Boolean mUploaded;
    
    public SurveyingTarget() {
        
    }

    /**
     * @return the {@link Landmark}
     */
    public Landmark getLandmark() {
        return mLandmark;
    }

    /**
     * @param landmark the {@link Landmark} to set
     */
    public void setLandmark(Landmark landmark) {
        this.mLandmark = landmark;
    }
    
    /**
     * @return RecentVisits
     */
    public Integer getRecentVisits() {
        return mRecentVisits;
    }
    
    /**
     * @param recentVisits the number of recent visits to set
     */
    public void setRecentVisits(Integer recentVisits) {
        mRecentVisits = recentVisits;
    }

    /**
     * @return the Asked
     */
    public Boolean getAsked() {
        return mAsked;
    }

    /**
     * @param asked the Asked to set
     */
    public void setAsked(Boolean asked) {
        this.mAsked = asked;
    }

    /**
     * @return the Skipped
     */
    public Boolean getSkipped() {
        return mSkipped;
    }

    /**
     * @param skipped the Skipped to set
     */
    public void setSkipped(Boolean skipped) {
        this.mSkipped = skipped;
    }

    /**
     * @return the DirectionEstimate
     */
    public Float getDirectionEstimate() {
        return mDirectionEstimate;
    }

    /**
     * @param directionEstimate the DirectionEstimate to set
     */
    public void setDirectionEstimate(Float directionEstimate) {
        this.mDirectionEstimate = directionEstimate;
    }

    /**
     * @return the DistanceEstimate
     */
    public Double getDistanceEstimate() {
        return mDistanceEstimate;
    }

    /**
     * @param distanceEstimate the DistanceEstimate to set
     */
    public void setDistanceEstimate(Double distanceEstimate) {
        this.mDistanceEstimate = distanceEstimate;
    }

    /**
     * @return the DistanceEstimateUnits
     */
    public String getDistanceEstimateUnits() {
        return mDistanceEstimateUnits;
    }

    /**
     * @param distanceEstimateUnits the DistanceEstimateUnits to set
     */
    public void setDistanceEstimateUnits(String distanceEstimateUnits) {
        this.mDistanceEstimateUnits = distanceEstimateUnits;
    }

    /**
     * @return the EstimateDate
     */
    public Date getEstimateDate() {
        return mEstimateDate;
    }

    /**
     * @param estimateDate the EstimateDate to set
     */
    public void setEstimateDate(Date estimateDate) {
        this.mEstimateDate = estimateDate;
    }
    
    /**
     * @return the Uploaded
     */
    public Boolean getUploaded() {
        return mUploaded;
    }

    /**
     * @param uploaded the Uploaded to set
     */
    public void setUploaded(Boolean uploaded) {
        this.mUploaded = uploaded;
    }

    @Override
    public int compareTo(SurveyingTarget another) {
        return another.getRecentVisits().compareTo(this.mRecentVisits);
    }

}
