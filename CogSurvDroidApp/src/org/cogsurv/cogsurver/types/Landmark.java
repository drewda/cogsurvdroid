package org.cogsurv.cogsurver.types;

public class Landmark implements CogSurvType {
    private int mLocalId;
    private int mServerId;
    private int mUserId;
    private String mFoursquareVenueId;
    private String mName;
    private String mAddress;
    private String mCity;
    private String mState;
    private String mZip;
    private double mLatitude;
    private double mLongitude;
    
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
    public void setUserId (int userId) {
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
    public double getLatitude() {
        return mLatitude;
    }
    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }
}
