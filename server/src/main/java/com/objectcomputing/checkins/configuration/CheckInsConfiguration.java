package com.objectcomputing.checkins.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("check-ins")
public interface CheckInsConfiguration {

    String getWebAddress();
}
