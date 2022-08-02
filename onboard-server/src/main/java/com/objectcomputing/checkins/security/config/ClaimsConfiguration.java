package com.objectcomputing.checkins.security.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;

@ConfigurationProperties(ClaimsConfiguration.PREFIX)
public class ClaimsConfiguration {
    public static final String PREFIX = AuthenticationConfigurationProperties.PREFIX + ".claims";

    private static final String DEFAULT_ACCESSOR_ID_NAME = "accessorId";
    private static final String DEFAULT_ACCESSOR_SOURCE_NAME = "accessorSource";

    private String accessorId = DEFAULT_ACCESSOR_ID_NAME;
    private String accessorSource = DEFAULT_ACCESSOR_SOURCE_NAME;

    public String getAccessorId() {
        return accessorId;
    }

    public void setAccessorId(String accessorId) {
        this.accessorId = accessorId;
    }

    public String getAccessorSource() {
        return accessorSource;
    }

    public void setAccessorSource(String accessorSource) {
        this.accessorSource = accessorSource;
    }
}
