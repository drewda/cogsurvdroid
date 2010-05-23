package org.cogsurv.droid.types;

import java.util.Date;

public class DirectionDistanceEstimate implements CogSurvType {
    private String mLocalId;
    private String mServerId;
    private String mUserId;
    private String mLandmarkVisitLocalId;
    private String mLandmarkVisitServerId;
    private Date mDatetime;
    private String mDirectionEstimate;
    private String mDistanceEstimate;
    private String mDistanceEstimateUnits;
    private String mStartLandmarkId;
    private String mTargetLandmarkId;
    
    public String getLocalId() {
        return mLocalId;
    }
    public void setLocalId(String mLocalId) {
        this.mLocalId = mLocalId;
    }
    public String getServerId() {
        return mServerId;
    }
    public void setServerId(String mServerId) {
        this.mServerId = mServerId;
    }
    public String getUserId() {
        return mUserId;
    }
    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }
    public String getLandmarkVisitLocalId() {
        return mLandmarkVisitLocalId;
    }
    public void setLandmarkVisitLocalId(String mLandmarkVisitLocalId) {
        this.mLandmarkVisitLocalId = mLandmarkVisitLocalId;
    }
    public String getLandmarkVisitServerId() {
        return mLandmarkVisitServerId;
    }
    public void setLandmarkVisitServerId(String mLandmarkVisitServerId) {
        this.mLandmarkVisitServerId = mLandmarkVisitServerId;
    }
    public Date getDatetime() {
        return mDatetime;
    }
    public void setDatetime(Date mDatetime) {
        this.mDatetime = mDatetime;
    }
    public String getDirectionEstimate() {
        return mDirectionEstimate;
    }
    public void setDirectionEstimate(String mDirectionEstimate) {
        this.mDirectionEstimate = mDirectionEstimate;
    }
    public String getDistanceEstimate() {
        return mDistanceEstimate;
    }
    public void setDistanceEstimate(String mDistanceEstimate) {
        this.mDistanceEstimate = mDistanceEstimate;
    }
    public String getDistanceEstimateUnits() {
        return mDistanceEstimateUnits;
    }
    public void setDistanceEstimateUnits(String mDistanceEstimateUnits) {
        this.mDistanceEstimateUnits = mDistanceEstimateUnits;
    }
    public String getStartLandmarkId() {
        return mStartLandmarkId;
    }
    public void setStartLandmarkId(String mStartLandmarkId) {
        this.mStartLandmarkId = mStartLandmarkId;
    }
    public String getTargetLandmarkId() {
        return mTargetLandmarkId;
    }
    public void setTargetLandmarkId(String mTargetLandmarkId) {
        this.mTargetLandmarkId = mTargetLandmarkId;
    }
}
