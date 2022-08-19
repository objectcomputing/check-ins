package com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6;

import java.util.Objects;

public class Srp6Challenge {
    private String salt;
    private String b;

    public Srp6Challenge() {
    }

    public Srp6Challenge(String salt, String b) {
        this.salt = salt;
        this.b = b;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Srp6Challenge that = (Srp6Challenge) o;
        return Objects.equals(salt, that.salt) && Objects.equals(b, that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salt, b);
    }

    @Override
    public String toString() {
        return "Srp6Challenge{" +
                "salt='" + salt + '\'' +
                ", b='" + b + '\'' +
                '}';
    }
}
