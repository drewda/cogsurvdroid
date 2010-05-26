package org.cogsurv.cogsurver.types;

public class Region implements CogSurvType {
    private int mLocalId;
    private int mServerId;
    private String mName;
    private int mUserId;
    
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
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }
    public int getUserId() {
        return mUserId;
    }
    public void setUserId(int userId) {
        mUserId = userId;
    }
}
