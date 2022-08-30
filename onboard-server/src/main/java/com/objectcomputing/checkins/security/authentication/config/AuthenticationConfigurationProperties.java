package com.objectcomputing.checkins.security.authentication.config;

import com.objectcomputing.checkins.OnboardConfigurationProperties;
import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties(AuthenticationConfigurationProperties.PREFIX)
public class AuthenticationConfigurationProperties implements AuthenticationConfiguration {
    public static final String PREFIX = OnboardConfigurationProperties.PREFIX + ".authentication";
}
