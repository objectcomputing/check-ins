package com.objectcomputing.checkins.auth.commons;


import java.util.Objects;

public class SecretsRequest {
    private String emailAddress;
    private String secret;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecretsRequest that = (SecretsRequest) o;
        return Objects.equals(emailAddress, that.emailAddress) && Objects.equals(secret, that.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress, secret);
    }
}
