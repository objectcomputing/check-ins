package com.objectcomputing.checkins.auth.commons;

import java.util.Objects;

public class ChallengeRequest {
    private String identity;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeRequest that = (ChallengeRequest) o;
        return Objects.equals(identity, that.identity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity);
    }
}
