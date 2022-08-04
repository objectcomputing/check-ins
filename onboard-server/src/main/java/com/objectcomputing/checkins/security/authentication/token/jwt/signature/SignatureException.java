package com.objectcomputing.checkins.security.authentication.token.jwt.signature;

import com.objectcomputing.geoai.core.GeoAiRuntimeException;

public class SignatureException extends GeoAiRuntimeException {
    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
