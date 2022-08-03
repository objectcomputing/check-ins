package com.objectcomputing.checkins.security.authentication.token.jwt.signature.config;

import com.objectcomputing.geoai.security.token.jwt.config.JsonWebTokenConfigurationProperties;
import io.micronaut.context.annotation.ConfigurationProperties;

import static com.objectcomputing.geoai.security.config.SecurityConfigurationProperties.DEFAULT_DISABLED;

@ConfigurationProperties(SignatureConfigurationProperties.PREFIX)
public class SignatureConfigurationProperties implements SignatureConfiguration {
    public static final String PREFIX = JsonWebTokenConfigurationProperties.PREFIX + ".signatures";

    private String secret;
    private String algorithm;
    private boolean base64 = DEFAULT_DISABLED;

    public SignatureConfigurationProperties() {

    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public boolean isBase64() {
        return base64;
    }

    public void setBase64(boolean base64) {
        this.base64 = base64;
    }
}
