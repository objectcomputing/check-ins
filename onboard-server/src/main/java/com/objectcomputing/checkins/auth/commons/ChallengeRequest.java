package com.objectcomputing.checkins.auth.commons;


import java.util.Objects;

public class ChallengeRequest {
    private String identity;
    private String scope;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeRequest that = (ChallengeRequest) o;
        return Objects.equals(identity, that.identity) && Objects.equals(scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, scope);
    }

    @Override
    public String toString() {
        return "ChallengeRequest{" +
                "identity='" + identity + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
