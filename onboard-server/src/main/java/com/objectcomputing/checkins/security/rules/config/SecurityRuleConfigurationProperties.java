package com.objectcomputing.checkins.security.rules.config;

import com.objectcomputing.geoai.security.config.SecurityConfigurationProperties;

import static com.objectcomputing.geoai.security.config.SecurityConfigurationProperties.DEFAULT_ENABLED;

public class SecurityRuleConfigurationProperties implements SecurityRuleConfiguration {

    public static final int ORDER = SecurityConfigurationProperties.ORDER - 100;

    private boolean enabled = DEFAULT_ENABLED;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
