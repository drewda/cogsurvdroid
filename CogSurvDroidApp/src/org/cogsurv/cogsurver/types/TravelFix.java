package org.cogsurv.cogsurver.types;

import java.util.Date;

public class TravelFix implements CogSurvType {
    private int mLocalId;
    private int mServerId;
    private int mUserId;
    private double mLatitude;
    private double mLongitude;
    private float mAltitude;
    private float mSpeed;
    private float mAccuracy;
    private String mPositioningMethod;
    private String mTravelMode;
    private Date mDatetime;
    
    public int getLocalId() {
        return mLocalId;
    }
    public void setLocalId(int localId) {
        this.mLocalId = localId;
    }
    public int getServerId() {
        return mServerId;
    }
    public void setServerId(int serverId) {
        this.mServerId = serverId;
    }
    public int getUserId() {
        return mUserId;
    }
    public void setUserId(int mUserId) {
        this.mUserId = mUserId;
    }
    public double getLatitude() {
        return mLatitude;
    }
    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }
    public float getAltitude() {
        return mAltitude;
    }
    public void setAltitude(float mAltitude) {
        this.mAltitude = mAltitude;
    }
    public float getSpeed() {
        return mSpeed;
    }
    public void setSpeed(float mSpeed) {
        this.mSpeed = mSpeed;
    }
    public float getAccuracy() {
        return mAccuracy;
    }
    public void setAccuracy(float mAccuracy) {
        this.mAccuracy = mAccuracy;
    }
    public String getPositioningMethod() {
        return mPositioningMethod;
    }
    public void setPositioningMethod(String mPositioningMethod) {
        this.mPositioningMethod = mPositioningMethod;
    }
    public String getTravelMode() {
        return mTravelMode;
    }
    public void setTravelMode(String mTravelMode) {
        this.mTravelMode = mTravelMode;
    }
    public Date getDatetime() {
        return mDatetime;
    }
    public void setDatetime(Date mDatetime) {
        this.mDatetime = mDatetime;
    }
}
