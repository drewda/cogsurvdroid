package org.cogsurv.cogsurver.types;

public class User implements CogSurvType {
    private String mId;
    private String firstName;
    private String lastName;
    private String email;
    private String foursquareUserId;
    
    public String getId() {
        return mId;
    }
    public void setId(String id) {
        this.mId = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFullName() {
      return this.firstName + " " + this.lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFoursquareUserId() {
        return foursquareUserId;
    }
    public void setFoursquareUserId(String foursquareUserId) {
        this.foursquareUserId = foursquareUserId;
    }
}
