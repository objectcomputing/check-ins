package com.objectcomputing.checkins.security.authentication;

import com.objectcomputing.geoai.core.account.Account;
import com.objectcomputing.geoai.platform.token.model.Token;
import com.objectcomputing.geoai.security.token.AbstractTokenAuthenticatedActor;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenClaims;

public class PlatformAuthenticatedActor<A extends Account> extends AbstractTokenAuthenticatedActor<A, Token> {

    protected PlatformAuthenticatedActor() {
    }

    public PlatformAuthenticatedActor(JsonWebTokenClaims claims, A account, PlatformTokenAuthenticationTicket ticket) {
        super(claims, account, ticket);
    }

    public PlatformAuthenticatedActor(JsonWebTokenClaims claims, A account, String tokenValue, Token token) {
        this(claims, account, new PlatformTokenAuthenticationTicket(tokenValue, token));
    }

    public Token getToken() {
        return getTicket().getToken();
    }

    public String getTokenValue() {
        return getTicket().getTokenValue();
    }
}
