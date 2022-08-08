package com.objectcomputing.checkins.security.authentication.srp6;

import java.util.Objects;

public class Srp6Credentials {
    private String emailAddress;
    private String salt;
    private String verifier;

    public Srp6Credentials() {
    }

    public Srp6Credentials(String emailAddress, String salt, String verifier) {
        this.emailAddress = emailAddress;
        this.salt = salt;
        this.verifier = verifier;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Srp6Credentials that = (Srp6Credentials) o;
        return Objects.equals(emailAddress, that.emailAddress) && Objects.equals(salt, that.salt) && Objects.equals(verifier, that.verifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress, salt, verifier);
    }
}
