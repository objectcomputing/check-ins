package com.objectcomputing.checkins.services.onboardeecreate.security.token;

import com.objectcomputing.checkins.services.onboardeecreate.newhire.util.BuildableHashMap;
import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.auth.AuthenticatableAccount;
import com.objectcomputing.checkins.services.onboardeecreate.security.authorization.AuthorizationToken;
import io.micronaut.security.authentication.ClientAuthentication;
import io.micronaut.security.token.generator.TokenGenerator;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;

@Singleton
public class AuthorizationTokenBuilder {
    public static final int EXPIRATION = 24 * 60 * 60;
    private final TokenGenerator tokenGenerator;

    public AuthorizationTokenBuilder(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    public AuthorizationToken build(AuthenticatableAccount authenticatableAccount) {
        Map<String,Object> attributes = new BuildableHashMap<String,Object>()
                .build("id", authenticatableAccount.getId().toString())
                .build("state", authenticatableAccount.getState())
                .build("roles", List.of("NewHire"));

        return new AuthorizationToken(
                authenticatableAccount.getId(),
                tokenGenerator.generateToken(new ClientAuthentication(
                        authenticatableAccount.getEmailAddress(), attributes), EXPIRATION).orElse(""));
    }
}
