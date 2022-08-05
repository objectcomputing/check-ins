package com.objectcomputing.checkins.security.authentication.token;

import com.objectcomputing.geoai.core.account.Account;
import com.objectcomputing.geoai.security.authentication.AuthenticatedActor;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenClaims;

public interface TokenAuthenticatedActor<A extends Account, T extends TokenRoot> extends AuthenticatedActor<JsonWebTokenClaims, A, TokenAuthenticationTicket<T>> {
}
