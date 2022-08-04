package com.objectcomputing.checkins.security.config;

import com.objectcomputing.geoai.security.config.SecurityConfigurationProperties;
import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties(TokenConfigurationProperties.PREFIX)
public class TokenConfigurationProperties implements TokenConfiguration {
    public static final String PREFIX = SecurityConfigurationProperties.PREFIX + ".token";
}
