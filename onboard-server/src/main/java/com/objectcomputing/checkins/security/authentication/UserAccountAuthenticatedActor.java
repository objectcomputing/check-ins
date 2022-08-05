package com.objectcomputing.checkins.security.authentication;

import com.objectcomputing.geoai.platform.account.model.UserAccount;
import com.objectcomputing.geoai.platform.token.model.Token;
import com.objectcomputing.geoai.security.token.jwt.JsonWebTokenClaims;

public class UserAccountAuthenticatedActor extends PlatformAuthenticatedActor<UserAccount> {
    public UserAccountAuthenticatedActor(JsonWebTokenClaims claims, UserAccount userAccount, String tokenValue, Token token) {
        super(claims, userAccount, tokenValue, token);
    }
}
