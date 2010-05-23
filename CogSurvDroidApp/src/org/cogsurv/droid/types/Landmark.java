package org.cogsurv.droid.types;

public class Landmark implements CogSurvType {
    private String mLocalId;
    private String mServerId;
    private String mUserId;
    private String mFoursquareVenueId;
    private String mName;
    private String mAddress;
    private String mCity;
    private String mState;
    private String mZip;
    private String mIcon;
    private String mLatitude;
    private String mLongitude;
    
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
    public void setUserId (String userId) {
        this.mUserId = userId;
    }
    public String getFoursquareVenueId() {
        return mFoursquareVenueId;
    }
    public void setFoursquareVenueId(String foursquareVenueId) {
        this.mFoursquareVenueId = foursquareVenueId;
    }
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        this.mName = name;
    }
    public String getAddress() {
        return mAddress;
    }
    public void setAddress(String address) {
        this.mAddress = address;
    }
    public String getCity() {
        return mCity;
    }
    public void setCity(String city) {
        this.mCity = city;
    }
    public String getState() {
        return mState;
    }
    public void setState(String state) {
        this.mState = state;
    }
    public String getZip() {
        return mZip;
    }
    public void setZip(String zip) {
        this.mZip = zip;
    }
    public String getIcon() {
        return mIcon;
    }
    public void setIcon(String icon) {
        this.mIcon = icon;
    }
    public String getLatitude() {
        return mLatitude;
    }
    public void setLatitude(String latitude) {
        this.mLatitude = latitude;
    }
    public String getLongitude() {
        return mLongitude;
    }
    public void setLongitude(String longitude) {
        this.mLongitude = longitude;
    }
}
