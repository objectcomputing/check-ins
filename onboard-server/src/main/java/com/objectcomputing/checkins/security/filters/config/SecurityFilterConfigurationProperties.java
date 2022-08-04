package com.objectcomputing.checkins.security.filters.config;

import com.objectcomputing.geoai.security.config.SecurityConfigurationProperties;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.http.annotation.Filter;

import static com.objectcomputing.geoai.security.config.SecurityConfigurationProperties.DEFAULT_ENABLED;

@ConfigurationProperties(SecurityFilterConfigurationProperties.PREFIX)
public class SecurityFilterConfigurationProperties implements SecurityFilterConfiguration {
    public static final String PREFIX = SecurityConfigurationProperties.PREFIX + ".filter";

    private boolean enabled = DEFAULT_ENABLED;

    private String pattern = Filter.MATCH_ALL_PATTERN;

    public SecurityFilterConfigurationProperties() {
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getPattern() {
        return null;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
