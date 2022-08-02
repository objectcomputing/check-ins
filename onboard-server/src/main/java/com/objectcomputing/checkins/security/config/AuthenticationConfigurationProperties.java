package com.objectcomputing.checkins.security.config;

import com.objectcomputing.checkins.security.config.SecurityConfigurationProperties;
import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties(AuthenticationConfigurationProperties.PREFIX)
public class AuthenticationConfigurationProperties implements AuthenticationConfiguration {
    public static final String PREFIX = SecurityConfigurationProperties.PREFIX + ".authentication";

    public static final int ORDER = SecurityConfigurationProperties.ORDER - 100;

}
