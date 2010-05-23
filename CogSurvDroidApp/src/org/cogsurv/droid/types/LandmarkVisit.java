package org.cogsurv.droid.types;

public class LandmarkVisit implements CogSurvType {
    private String mLocalId;
    private String mServerId;
    private String mLandmarkId;
    private String mUserId;
    
    public String getLocalId() {
        return mLocalId;
    }
    public void setLocalId(String localId) {
        mLocalId = localId;
    }
    public String getServerId() {
        return mServerId;
    }
    public void setServerId(String serverId) {
        mServerId = serverId;
    }
    public String getLandmarkId() {
        return mLandmarkId;
    }
    public void setLandmarkId(String landmarkId) {
        mLandmarkId = landmarkId;
    }
    public String getUserId() {
        return mUserId;
    }
    public void setUserId(String userId) {
        mUserId = userId;
    }
}
