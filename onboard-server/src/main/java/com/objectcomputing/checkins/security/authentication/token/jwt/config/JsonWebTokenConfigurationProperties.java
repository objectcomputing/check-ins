package com.objectcomputing.checkins.security.authentication.token.jwt.config;

import com.objectcomputing.geoai.security.token.config.TokenConfigurationProperties;
import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties(JsonWebTokenConfigurationProperties.PREFIX)
public class JsonWebTokenConfigurationProperties implements JsonWebTokenConfiguration {
    public static final String PREFIX = TokenConfigurationProperties.PREFIX + ".jwt";

    private String issuer;
    private long lease;

    public JsonWebTokenConfigurationProperties() {

    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public long getLease() {
        return lease;
    }

    public void setLease(long lease) {
        this.lease = lease;
    }
}
