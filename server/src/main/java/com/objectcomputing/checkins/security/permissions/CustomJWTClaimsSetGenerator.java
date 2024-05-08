package com.objectcomputing.checkins.security.permissions;

import com.nimbusds.jwt.JWTClaimsSet;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.claims.ClaimsAudienceProvider;
import io.micronaut.security.token.claims.JtiGenerator;
import io.micronaut.security.token.config.TokenConfiguration;
import io.micronaut.security.token.jwt.generator.claims.JWTClaimsSetGenerator;
import jakarta.inject.Singleton;

@Singleton
@Replaces(bean = JWTClaimsSetGenerator.class)
public class CustomJWTClaimsSetGenerator extends JWTClaimsSetGenerator {

    public CustomJWTClaimsSetGenerator(TokenConfiguration tokenConfiguration,
                                       @Nullable JtiGenerator jwtIdGenerator,
                                       @Nullable ClaimsAudienceProvider claimsAudienceProvider,
                                       @Nullable ApplicationConfiguration applicationConfiguration) {
        super(tokenConfiguration, jwtIdGenerator, claimsAudienceProvider, applicationConfiguration);
    }

    @Override
    protected void populateWithAuthentication(JWTClaimsSet.Builder builder, Authentication authentication) {
        super.populateWithAuthentication(builder, authentication);
        if (authentication instanceof ExtendedAuthentication) {
            builder.claim("permissions", ((ExtendedAuthentication)authentication).getPermissions());
        }
    }
}
