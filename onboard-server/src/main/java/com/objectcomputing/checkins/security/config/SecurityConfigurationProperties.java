package com.objectcomputing.checkins.security.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties(SecurityConfigurationProperties.PREFIX)
public class SecurityConfigurationProperties implements SecurityConfiguration {
    public static final String PREFIX = "geoai.security";

    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_DISABLED = false;

    public static final Integer ORDER = 0;

    private boolean enabled = DEFAULT_ENABLED;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
