package org.cogsurv.droid.types;

import java.util.Date;

public class TravelFix implements CogSurvType {
    private String mLocalId;
    private String mServerId;
    private String mUserId;
    private String mLatitude;
    private String mLongitude;
    private String mAltitude;
    private String mSpeed;
    private String mAccuracy;
    private String mPositioningMethod;
    private String mTravelMode;
    private Date mDatetime;
    
    public String getLocalId() {
        return mLocalId;
    }
    public void setLocalId(String localId) {
        this.mLocalId = localId;
    }
    public String getServerId() {
        return mServerId;
    }
    public void setServerId(String serverId) {
        this.mServerId = serverId;
    }
    public String getUserId() {
        return mUserId;
    }
    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }
    public String getLatitude() {
        return mLatitude;
    }
    public void setLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }
    public String getLongitude() {
        return mLongitude;
    }
    public void setLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }
    public String getAltitude() {
        return mAltitude;
    }
    public void setAltitude(String mAltitude) {
        this.mAltitude = mAltitude;
    }
    public String getSpeed() {
        return mSpeed;
    }
    public void setSpeed(String mSpeed) {
        this.mSpeed = mSpeed;
    }
    public String getAccuracy() {
        return mAccuracy;
    }
    public void setAccuracy(String mAccuracy) {
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
