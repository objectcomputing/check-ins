package com.objectcomputing.checkins.newhire.endpoint;



import java.util.Objects;


public class UserAccountConfig {
    private String emailAddress;
    private String handle;

    private String firstName;
    private String lastName;

    private String salt;
    private String primaryVerifier;
    private String secondaryVerifier;

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPrimaryVerifier() {
        return primaryVerifier;
    }

    public void setPrimaryVerifier(String primaryVerifier) {
        this.primaryVerifier = primaryVerifier;
    }

    public String getSecondaryVerifier() {
        return secondaryVerifier;
    }

    public void setSecondaryVerifier(String secondaryVerifier) {
        this.secondaryVerifier = secondaryVerifier;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccountConfig that = (UserAccountConfig) o;
        return Objects.equals(emailAddress, that.emailAddress) && Objects.equals(handle, that.handle) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(salt, that.salt) && Objects.equals(primaryVerifier, that.primaryVerifier) && Objects.equals(secondaryVerifier, that.secondaryVerifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress, handle, firstName, lastName, salt, primaryVerifier, secondaryVerifier);
    }

    @Override
    public String toString() {
        return "UserAccountConfig{" +
                "emailAddress='" + emailAddress + '\'' +
                ", handle='" + handle + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", salt='" + salt + '\'' +
                ", primaryVerifier='" + primaryVerifier + '\'' +
                ", secondaryVerifier='" + secondaryVerifier + '\'' +
                '}';
    }
}
