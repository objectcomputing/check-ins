package com.objectcomputing.checkins.security.authentication.token.jwt;

import com.objectcomputing.checkins.security.authentication.Claims;

public interface JsonWebTokenClaims extends Claims {
    Long getExpirationTime();
    Long getIssuedTime();
    Long getNotBeforeTime();
}
