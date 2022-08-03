package com.objectcomputing.checkins.security.authentication.token.commons;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AuthorizationToken {
    private String token;
    private long createdTime;
    private long issuedTime;
    private long lease;
    private Date expirationTime;
    private List<String> policies;
    private Map<String, String> meta;
    private boolean renewable = true;

    public AuthorizationToken(String token, long createdTime, long issuedTime, long lease, List<String> policies, Map<String, String> meta, boolean renewable) {
        this.token = token;
        this.createdTime = createdTime;
        this.issuedTime = issuedTime;
        this.lease = lease;
        this.expirationTime = new Date(issuedTime + lease);
        this.policies = policies;
        this.meta = meta;
        this.renewable = renewable;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getIssuedTime() {
        return issuedTime;
    }

    public void setIssuedTime(long issuedTime) {
        this.issuedTime = issuedTime;
    }

    public long getLease() {
        return lease;
    }

    public void setLease(long lease) {
        this.lease = lease;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public List<String> getPolicies() {
        return policies;
    }

    public void setPolicies(List<String> policies) {
        this.policies = policies;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public boolean isRenewable() {
        return renewable;
    }

    public void setRenewable(boolean renewable) {
        this.renewable = renewable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationToken that = (AuthorizationToken) o;
        return createdTime == that.createdTime && issuedTime == that.issuedTime && lease == that.lease && renewable == that.renewable && Objects.equals(token, that.token) && Objects.equals(expirationTime, that.expirationTime) && Objects.equals(policies, that.policies) && Objects.equals(meta, that.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, createdTime, issuedTime, lease, expirationTime, policies, meta, renewable);
    }

    @Override
    public String toString() {
        return "AuthorizationToken{" +
                "token='" + token + '\'' +
                ", createdTime=" + createdTime +
                ", issuedTime=" + issuedTime +
                ", lease=" + lease +
                ", expirationTime=" + expirationTime +
                ", policies=" + policies +
                ", meta=" + meta +
                ", renewable=" + renewable +
                '}';
    }
}
