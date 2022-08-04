package com.objectcomputing.checkins.security.authentication.token.jwt.signature;

import com.objectcomputing.checkins.security.authentication.token.TokenSignature;
import io.micronaut.core.order.Ordered;

public interface SignatureVerifier<C extends SignatureConfig, S extends TokenSignature> extends Ordered {
    boolean verify(C config, byte[] signingInput, S signature);
}
