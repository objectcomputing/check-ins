package com.objectcomputing.checkins.security.authentication;

import com.objectcomputing.geoai.platform.token.model.Token;
import com.objectcomputing.geoai.security.token.TokenAuthenticationTicket;
import lombok.Data;

@Data
public class PlatformTokenAuthenticationTicket implements TokenAuthenticationTicket<Token> {
    private String tokenValue;
    private Token token;

    public PlatformTokenAuthenticationTicket(String tokenValue, Token token) {
        this.tokenValue = tokenValue;
        this.token = token;
    }
}
