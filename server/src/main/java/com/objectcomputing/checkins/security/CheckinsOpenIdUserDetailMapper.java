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
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Replaces(DefaultOpenIdUserDetailsMapper.class)
public class CheckinsOpenIdUserDetailMapper implements OpenIdUserDetailsMapper {

    private static final Logger LOG = LoggerFactory.getLogger(CheckinsOpenIdUserDetailMapper.class);
    private final MemberProfileRepository memberProfileRepository;
    private final RoleRepository roleRepository;

    public CheckinsOpenIdUserDetailMapper(MemberProfileRepository memberProfileRepository,
                                          RoleRepository roleRepository) {
        this.memberProfileRepository = memberProfileRepository;
        this.roleRepository = roleRepository;
    }

    @NonNull
    @Override
    public UserDetails createUserDetails(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims) {
        Map<String, Object> claims = buildAttributes(providerName, tokenResponse, openIdClaims);
        List<String> roles = getRoles(openIdClaims);
        String username = openIdClaims.getSubject();
        return new UserDetails(username, roles, claims);
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
                        LOG.info("MemberProfile of the user:", memberProfile);
                        roles.addAll(roleRepository.findByMemberid(memberProfile.getId())
                                .stream()
                                .map(role -> role.getRole().toString())
                                .collect(Collectors.toList()));
                });

        LOG.info("Email address of the user:", openIdClaims.getEmail());
        LOG.info("List of roles from roleRepository:", roles);
        return roles;
    }
}
