package com.objectcomputing.checkins.security.authentication;

import java.util.Map;
import java.util.Optional;

public interface Claims {
    String getIssuer();
    String getSubject();
    String getAudience();
    String getIdentifier();
    Map<String,Object> getPublicClaims();
    Optional<Object> getPublicClaim(String claim);
    <T> Optional<T> getPublicClaim(String claim, Class<T> type);
}
