package com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6;


import java.util.Objects;

public class Srp6Secrets {
    private String salt;
    private String verifier;

    public Srp6Secrets() {
    }

    public Srp6Secrets(String salt, String verifier) {
        this.salt = salt;
        this.verifier = verifier;
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
        Srp6Secrets that = (Srp6Secrets) o;
        return Objects.equals(salt, that.salt) && Objects.equals(verifier, that.verifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salt, verifier);
    }

    @Override
    public String toString() {
        return "Srp6Secrets{" +
                "salt='" + salt + '\'' +
                ", verifier='" + verifier + '\'' +
                '}';
    }
}
