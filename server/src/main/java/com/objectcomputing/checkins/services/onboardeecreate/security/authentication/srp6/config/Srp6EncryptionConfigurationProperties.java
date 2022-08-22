package com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.config;

import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.config.AuthenticationConfigurationProperties;
import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties(Srp6EncryptionConfigurationProperties.PREFIX)
public class Srp6EncryptionConfigurationProperties implements Srp6EncryptionConfiguration {
    public static final String PREFIX = AuthenticationConfigurationProperties.PREFIX + ".srp6.encryption";

    private String algorithm;
    private Integer bitSize;

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public Integer getBitSize() {
        return bitSize;
    }

    public void setBitSize(Integer bitSize) {
        this.bitSize = bitSize;
    }
}
