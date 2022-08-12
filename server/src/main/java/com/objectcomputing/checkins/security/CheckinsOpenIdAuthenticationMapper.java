package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.token.response.*;
import io.micronaut.security.token.config.TokenConfiguration;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("google")
@Singleton
public class CheckinsOpenIdAuthenticationMapper implements OpenIdAuthenticationMapper {

    private static final Logger LOG = LoggerFactory.getLogger(CheckinsOpenIdAuthenticationMapper.class);
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final RoleRepository roleRepository;
    private final TokenConfiguration tokenConfiguration;

    public CheckinsOpenIdAuthenticationMapper(MemberProfileRetrievalServices memberProfileRetrievalServices,
                                              RoleRepository roleRepository,
                                              TokenConfiguration tokenConfiguration) {
        LOG.info("Creating an instance of CheckinsOpenIdUserDetailMapper using the constructor");
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.roleRepository = roleRepository;
        this.tokenConfiguration = tokenConfiguration;
    }

    @NonNull
    public AuthenticationResponse createAuthentication(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims) {
        Map<String, Object> claims = buildAttributes(providerName, tokenResponse, openIdClaims);
        List<String> roles = getRoles(openIdClaims);
        String username = openIdClaims.getSubject();
        LOG.info("Creating new authentication for user: {}", username);
        return AuthenticationResponse.success(username, roles, claims);
    }

    @NonNull
    @Override
    public AuthenticationResponse createAuthenticationResponse(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims, @Nullable State state) {
        return createAuthentication(providerName, tokenResponse, openIdClaims);
    }

    /**
     * @param providerName  The OpenID provider name
     * @param tokenResponse The token response
     * @param openIdClaims  The OpenID claims
     * @return The attributes to set in the {@link Authentication}
     */
    protected Map<String, Object> buildAttributes(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims) {
        Map<String, Object> claims = new HashMap<>(openIdClaims.getClaims());
        JwtClaims.ALL_CLAIMS.forEach(claims::remove);
        claims.put(OauthAuthenticationMapper.PROVIDER_KEY, providerName);
        claims.put(OpenIdAuthenticationMapper.OPENID_TOKEN_KEY, tokenResponse.getIdToken());
        claims.put(tokenConfiguration.getRolesName(), getRoles(openIdClaims));
        return claims;
    }

    /**
     * @param openIdClaims The OpenID claims
     * @return The roles to set in the {@link Authentication}
     */
    protected List<String> getRoles(OpenIdClaims openIdClaims) {
        List<String> roles = new ArrayList<>();
        memberProfileRetrievalServices.findByWorkEmail(openIdClaims.getEmail())
                .ifPresent((memberProfile) -> {
                        LOG.info("MemberProfile of the user: {}", memberProfile);
                        roles.addAll(roleRepository.findUserRoles(memberProfile.getId())
                                .stream()
                                .map(role -> role.getRole())
                                .collect(Collectors.toList()));
                });

        LOG.info("Email address of the user: {}", openIdClaims.getEmail());
        LOG.info("List of roles from roleRepository: {}", roles);
        return roles;
    }
}
