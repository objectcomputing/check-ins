package com.objectcomputing.checkins.security.authentication.token;

import com.objectcomputing.geoai.core.account.Account;
import com.objectcomputing.geoai.security.authentication.AbstractAuthenticatedActor;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenClaims;

public abstract class AbstractTokenAuthenticatedActor<A extends Account, T extends TokenRoot> extends AbstractAuthenticatedActor<JsonWebTokenClaims, A, TokenAuthenticationTicket<T>>
                                                                                              implements TokenAuthenticatedActor<A, T> {
    protected AbstractTokenAuthenticatedActor() {
    }

    public AbstractTokenAuthenticatedActor(JsonWebTokenClaims claims, A account, TokenAuthenticationTicket<T> ticket) {
        super(claims, account, ticket);

    }
}
