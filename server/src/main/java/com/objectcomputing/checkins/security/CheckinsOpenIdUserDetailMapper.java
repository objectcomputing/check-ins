package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.token.response.*;
import io.micronaut.security.token.DefaultRolesFinder;
import io.micronaut.security.token.config.TokenConfiguration;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import ognl.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("google")
@Singleton
public class CheckinsOpenIdUserDetailMapper implements OpenIdUserDetailsMapper {

    private static final Logger LOG = LoggerFactory.getLogger(CheckinsOpenIdUserDetailMapper.class);
    private final MemberProfileRepository memberProfileRepository;
    private final RoleRepository roleRepository;
    private final TokenConfiguration tokenConfiguration;

    public CheckinsOpenIdUserDetailMapper(MemberProfileRepository memberProfileRepository,
                                          RoleRepository roleRepository,
                                          TokenConfiguration tokenConfiguration) {
        LOG.info("Creating an instance of CheckinsOpenIdUserDetailMapper using the constructor");
        this.memberProfileRepository = memberProfileRepository;
        this.roleRepository = roleRepository;
        this.tokenConfiguration = tokenConfiguration;
    }

    @NonNull
    @Override
    public UserDetails createUserDetails(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims) {
        Map<String, Object> claims = buildAttributes(providerName, tokenResponse, openIdClaims);
        List<String> roles = getRoles(openIdClaims);
        String username = openIdClaims.getSubject();
        UserDetails userDetails = new UserDetails(username, roles, claims);
        LOG.info("Creating new userdetails for user: {}", userDetails.getUsername());
        return userDetails;
    }

    @NonNull
    @Override
    public AuthenticationResponse createAuthenticationResponse(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims, @Nullable State state) {
        return createUserDetails(providerName, tokenResponse, openIdClaims);
    }

    /**
     * @param providerName  The OpenID provider name
     * @param tokenResponse The token response
     * @param openIdClaims  The OpenID claims
     * @return The attributes to set in the {@link UserDetails}
     */
    protected Map<String, Object> buildAttributes(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims) {
        Map<String, Object> claims = new HashMap<>(openIdClaims.getClaims());
        JwtClaims.ALL_CLAIMS.forEach(claims::remove);
        claims.put(OauthUserDetailsMapper.PROVIDER_KEY, providerName);
        claims.put(OpenIdUserDetailsMapper.OPENID_TOKEN_KEY, tokenResponse.getIdToken());
        claims.put(tokenConfiguration.getRolesName(), getRoles(openIdClaims));
        return claims;
    }

    /**
     * @param openIdClaims The OpenID claims
     * @return The roles to set in the {@link UserDetails}
     */
    protected List<String> getRoles(OpenIdClaims openIdClaims) {
        List<String> roles = new ArrayList<>();
        memberProfileRepository.findByWorkEmail(openIdClaims.getEmail())
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
