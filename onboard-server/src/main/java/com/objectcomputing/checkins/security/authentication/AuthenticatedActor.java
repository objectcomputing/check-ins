package com.objectcomputing.checkins.security.authentication;

import com.objectcomputing.checkins.services.commons.account.Account;

import java.security.Principal;

public interface AuthenticatedActor<C extends Claims, A extends Account, T extends AuthenticationTicket> extends Principal {
    AuthenticatedActor EMPTY = null;

    C getClaims();
    A getAccount();
    T getTicket();
}
