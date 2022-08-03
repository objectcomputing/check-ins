package com.objectcomputing.checkins.security.authentication.token.commons;

import com.objectcomputing.geoai.platform.token.model.Token;
import lombok.Data;

@Data
public class AuthorizationTokenSpec {
    private final Token token;
    private final String tokenSource;

    public AuthorizationTokenSpec(Token token, String tokenSource) {
        this.token = token;
        this.tokenSource = tokenSource;
    }
}
