package org.cogsurv.droid.types;

public class User implements CogSurvType {
    private String mId;
    private String mFirstName;
    private String mLastName;
    private String mFoursquareId;
    private String mEmailAddress;
    private String mPhoto;
    
    public String getId() {
        return mId;
    }
    public void setId(String id) {
        this.mId = id;
    }
    public String getFirstName() {
        return mFirstName;
    }
    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }
    public String getLastName() {
        return mLastName;
    }
    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }
    public String getFoursquareId() {
        return mFoursquareId;
    }
    public void setFoursquareId(String foursquareId) {
        this.mFoursquareId = foursquareId;
    }
    public String getEmailAddress() {
        return mEmailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.mEmailAddress = emailAddress;
    }
    public String getPhoto() {
        return mPhoto;
    }
    public void setPhoto(String photo) {
        this.mPhoto = photo;
    }
    
}
