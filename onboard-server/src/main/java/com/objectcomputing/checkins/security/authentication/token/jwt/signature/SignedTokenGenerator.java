package com.objectcomputing.checkins.security.authentication.token.jwt.signature;

import com.objectcomputing.geoai.security.token.TokenPayload;
import com.objectcomputing.geoai.security.token.TokenRoot;
import io.micronaut.core.order.Ordered;

public interface SignedTokenGenerator<R extends TokenRoot, P extends TokenPayload> extends Ordered {

    R sign(P payload) throws SignatureException;

}
