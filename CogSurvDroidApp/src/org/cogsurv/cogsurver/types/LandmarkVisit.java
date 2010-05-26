package org.cogsurv.cogsurver.types;

import java.util.Date;

public class LandmarkVisit implements CogSurvType {
    private int mLocalId;
    private int mServerId;
    private int mLandmarkId;
    private int mUserId;
    private Date mDatetime;
    
    public int getLocalId() {
        return mLocalId;
    }
    public void setLocalId(int localId) {
        mLocalId = localId;
    }
    public int getServerId() {
        return mServerId;
    }
    public void setServerId(int serverId) {
        mServerId = serverId;
    }
    public int getLandmarkId() {
        return mLandmarkId;
    }
    public void setLandmarkId(int landmarkId) {
        mLandmarkId = landmarkId;
    }
    public int getUserId() {
        return mUserId;
    }
    public void setUserId(int userId) {
        mUserId = userId;
    }
    public Date getDatetime() {
      return mDatetime;
    }
    public void setDatetime(Date datetime) {
      mDatetime = datetime;
    }
}
