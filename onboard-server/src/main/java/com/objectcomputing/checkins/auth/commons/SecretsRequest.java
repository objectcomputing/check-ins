package com.objectcomputing.checkins.auth.commons;


import java.util.Objects;

public class SecretsRequest {
    private String identity;
    private String secret;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
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
        return Objects.equals(identity, that.identity) && Objects.equals(secret, that.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, secret);
    }
}
