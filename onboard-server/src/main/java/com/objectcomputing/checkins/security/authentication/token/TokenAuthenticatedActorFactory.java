package com.objectcomputing.checkins.security.authentication.token;

import com.objectcomputing.geoai.core.account.Account;
import com.objectcomputing.geoai.security.authentication.AuthenticatedActor;
import com.objectcomputing.geoai.security.authentication.AuthenticatedActorFactory;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenClaims;

import java.util.Optional;

public interface TokenAuthenticatedActorFactory<A extends Account, T extends TokenRoot> extends
        AuthenticatedActorFactory<JsonWebTokenClaims, A, TokenAuthenticationTicket<T>, String> {

    default Optional<AuthenticatedActor> createOptionalAuthenticatedActor(JsonWebTokenClaims claims, String tokenValue) {
        return Optional.ofNullable(createAuthenticatedActor(claims, tokenValue));
    }
}
