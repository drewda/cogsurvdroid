package org.cogsurv.cogsurver.types;

import java.util.Date;

public class DirectionDistanceEstimate implements CogSurvType {
    private int mLocalId;
    private int mServerId;
    private int mUserId;
    private int mLandmarkVisitId;
    private Date mDatetime;
    private float mDirectionEstimate;
    private float mDistanceEstimate;
    private String mDistanceEstimateUnits;
    private int mStartLandmarkId;
    private int mTargetLandmarkId;
    
    public int getLocalId() {
        return mLocalId;
    }
    public void setLocalId(int mLocalId) {
        this.mLocalId = mLocalId;
    }
    public int getServerId() {
        return mServerId;
    }
    public void setServerId(int mServerId) {
        this.mServerId = mServerId;
    }
    public int getUserId() {
        return mUserId;
    }
    public void setUserId(int mUserId) {
        this.mUserId = mUserId;
    }
    public int getLandmarkVisitId() {
        return mLandmarkVisitId;
    }
    public void setLandmarkVisitId(int mLandmarkVisitId) {
        this.mLandmarkVisitId = mLandmarkVisitId;
    }
    public Date getDatetime() {
        return mDatetime;
    }
    public void setDatetime(Date mDatetime) {
        this.mDatetime = mDatetime;
    }
    public float getDirectionEstimate() {
        return mDirectionEstimate;
    }
    public void setDirectionEstimate(float mDirectionEstimate) {
        this.mDirectionEstimate = mDirectionEstimate;
    }
    public float getDistanceEstimate() {
        return mDistanceEstimate;
    }
    public void setDistanceEstimate(float mDistanceEstimate) {
        this.mDistanceEstimate = mDistanceEstimate;
    }
    public String getDistanceEstimateUnits() {
        return mDistanceEstimateUnits;
    }
    public void setDistanceEstimateUnits(String mDistanceEstimateUnits) {
        this.mDistanceEstimateUnits = mDistanceEstimateUnits;
    }
    public int getStartLandmarkId() {
        return mStartLandmarkId;
    }
    public void setStartLandmarkId(int mStartLandmarkId) {
        this.mStartLandmarkId = mStartLandmarkId;
    }
    public int getTargetLandmarkId() {
        return mTargetLandmarkId;
    }
    public void setTargetLandmarkId(int mTargetLandmarkId) {
        this.mTargetLandmarkId = mTargetLandmarkId;
    }
}
