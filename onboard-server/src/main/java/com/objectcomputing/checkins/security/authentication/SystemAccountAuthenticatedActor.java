package com.objectcomputing.checkins.security.authentication;

import com.objectcomputing.geoai.platform.system.model.SystemAccount;
import com.objectcomputing.geoai.platform.token.model.Token;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenClaims;

public class SystemAccountAuthenticatedActor extends PlatformAuthenticatedActor<SystemAccount> {
    public SystemAccountAuthenticatedActor(JsonWebTokenClaims claims, SystemAccount systemAccount, String tokenValue, Token token) {
        super(claims, systemAccount, tokenValue, token);
    }
}
