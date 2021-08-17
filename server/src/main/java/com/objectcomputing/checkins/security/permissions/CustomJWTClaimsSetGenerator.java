package com.objectcomputing.checkins.security.permissions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.objectcomputing.checkins.services.permissions.Permission;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.token.config.TokenConfiguration;
import io.micronaut.security.token.jwt.generator.claims.ClaimsAudienceProvider;
import io.micronaut.security.token.jwt.generator.claims.JWTClaimsSetGenerator;
import io.micronaut.security.token.jwt.generator.claims.JwtIdGenerator;

import javax.inject.Singleton;
import java.util.List;

@Singleton
@Replaces(bean = JWTClaimsSetGenerator.class)
public class CustomJWTClaimsSetGenerator extends JWTClaimsSetGenerator {

    public CustomJWTClaimsSetGenerator(TokenConfiguration tokenConfiguration,
                                       @Nullable JwtIdGenerator jwtIdGenerator,
                                       @Nullable ClaimsAudienceProvider claimsAudienceProvider,
                                       @Nullable ApplicationConfiguration applicationConfiguration) {
        super(tokenConfiguration, jwtIdGenerator, claimsAudienceProvider, applicationConfiguration);
    }

    @Override
    protected void populateWithUserDetails(JWTClaimsSet.Builder builder, UserDetails userDetails) {
        super.populateWithUserDetails(builder, userDetails);
        if (userDetails instanceof ExtendedUserDetails) {
            ExtendedUserDetails extended = (ExtendedUserDetails) userDetails;
            List<Permission> permissions = extended.getPermissions();

            // had to manually do the json serialization for some reason
            try {
                String json = new ObjectMapper().writeValueAsString(permissions);
                builder.claim("permissions", json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            // TODO figure out why id isn't included in each permission of the JWT (list of permissions does map to JSON entity?)
//            builder.claim("permissions", ((ExtendedUserDetails)userDetails).getPermissions());
//            System.out.println(builder.getClaims().get("permissions").toString());

        }
    }
}
