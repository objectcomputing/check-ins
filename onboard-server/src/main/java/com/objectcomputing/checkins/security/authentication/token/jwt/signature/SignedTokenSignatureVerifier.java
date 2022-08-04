package com.objectcomputing.checkins.security.authentication.token.jwt.signature;

import com.objectcomputing.geoai.security.token.TokenRoot;
import io.micronaut.core.order.Ordered;

public interface SignedTokenSignatureVerifier<T extends TokenRoot> extends Ordered {
    boolean verify(T token);
}
