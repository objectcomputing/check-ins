package com.objectcomputing.checkins.security.authentication.token.jwt.signature.secret;

import com.objectcomputing.geoai.security.token.jwt.signature.config.SignatureConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;

import static com.objectcomputing.geoai.security.config.SecurityConfigurationProperties.DEFAULT_DISABLED;

@EachProperty(SignatureConfigurationProperties.PREFIX)
@Context
public class SecretSignatureConfiguration {
    public static final String PREFIX = SignatureConfigurationProperties.PREFIX + ".secret";

    private final String name;
    private String secret;
    private String algorithm;
    private boolean base64 = DEFAULT_DISABLED;

    public SecretSignatureConfiguration(@Parameter String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
