package com.objectcomputing.checkins.newhire.commons;

import java.util.Objects;

public class NewHireAccountConfig {
    private String emailAddress;

    private String salt;
    private String primaryVerifier;
    private String secondaryVerifier;


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
        NewHireAccountConfig that = (NewHireAccountConfig) o;
        return Objects.equals(emailAddress, that.emailAddress) && Objects.equals(salt, that.salt) && Objects.equals(primaryVerifier, that.primaryVerifier) && Objects.equals(secondaryVerifier, that.secondaryVerifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress, salt, primaryVerifier, secondaryVerifier);
    }
}
