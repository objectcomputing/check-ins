package com.objectcomputing.checkins.security;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.token.response.*;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.*;

@Named("google")
@Singleton
public class SimpleOpenIdAuthenticationMapper implements OpenIdAuthenticationMapper {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleOpenIdAuthenticationMapper.class);

    public SimpleOpenIdAuthenticationMapper() {
        LOG.info("Creating an instance of CheckinsOpenIdUserDetailMapper using the constructor");
    }

    @NonNull
    public AuthenticationResponse createAuthentication(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims) {
        Map<String, Object> claims = buildAttributes(providerName, tokenResponse, openIdClaims);
        String username = openIdClaims.getSubject();
        LOG.info("Creating new authentication for user: {}", username);
        return AuthenticationResponse.success(username, Collections.EMPTY_LIST, claims);
    }

    @NonNull
    @Override
    public AuthenticationResponse createAuthenticationResponse(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims, @Nullable State state) {
        return createAuthentication(providerName, tokenResponse, openIdClaims);
    }

    /**
     * @param providerName  The OpenID provider name
     * @param tokenResponse The token response
     * @param openIdClaims  The OpenID claims
     * @return The attributes to set in the {@link Authentication}
     */
    protected Map<String, Object> buildAttributes(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims) {
        Map<String, Object> claims = new HashMap<>(openIdClaims.getClaims());
        JwtClaims.ALL_CLAIMS.forEach(claims::remove);
        claims.put(OauthAuthenticationMapper.PROVIDER_KEY, providerName);
        claims.put(OpenIdAuthenticationMapper.OPENID_TOKEN_KEY, tokenResponse.getIdToken());
        return claims;
    }
}
