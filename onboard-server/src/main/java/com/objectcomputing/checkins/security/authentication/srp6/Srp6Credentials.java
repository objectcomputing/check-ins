package com.objectcomputing.checkins.security.authentication.srp6;

import java.util.Objects;

public class Srp6Credentials {
    private String identity;
    private String salt;
    private String verifier;

    public Srp6Credentials() {
    }

    public Srp6Credentials(String identity, String salt, String verifier) {
        this.identity = identity;
        this.salt = salt;
        this.verifier = verifier;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
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
        return Objects.equals(identity, that.identity) && Objects.equals(salt, that.salt) && Objects.equals(verifier, that.verifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, salt, verifier);
    }

    @Override
    public String toString() {
        return "Srp6Credentials{" +
                "identity='" + identity + '\'' +
                ", salt='" + salt + '\'' +
                ", verifier='" + verifier + '\'' +
                '}';
    }
}
