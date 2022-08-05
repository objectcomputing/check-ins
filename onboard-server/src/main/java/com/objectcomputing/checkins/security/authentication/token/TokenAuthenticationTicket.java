package com.objectcomputing.checkins.security.authentication.token;

import com.objectcomputing.geoai.security.authentication.AuthenticationTicket;

public interface TokenAuthenticationTicket<T extends TokenRoot> extends AuthenticationTicket {
    String getTokenValue();

    T getToken();
}
