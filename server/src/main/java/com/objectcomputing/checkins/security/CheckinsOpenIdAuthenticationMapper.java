package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.token.response.OauthAuthenticationMapper;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdAuthenticationMapper;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdTokenResponse;
import io.micronaut.security.token.Claims;
import io.micronaut.security.token.config.TokenConfiguration;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Primary
@Named("google")
@Requires(env="google")
@Singleton
public class CheckinsOpenIdAuthenticationMapper implements OpenIdAuthenticationMapper {

    private static final Logger LOG = LoggerFactory.getLogger(CheckinsOpenIdAuthenticationMapper.class);
    private final MemberProfileRepository memberProfileRepository;
    private final RoleRepository roleRepository;
    private final TokenConfiguration tokenConfiguration;
    private final Scheduler ioScheduler;

    public CheckinsOpenIdAuthenticationMapper(MemberProfileRepository memberProfileRepository,
                                              RoleRepository roleRepository,
                                              TokenConfiguration tokenConfiguration,
                                              @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {

        LOG.info("Creating an instance of CheckinsOpenIdUserDetailMapper using the constructor");
        this.memberProfileRepository = memberProfileRepository;
        this.roleRepository = roleRepository;
        this.tokenConfiguration = tokenConfiguration;
        this.ioScheduler = Schedulers.fromExecutor(ioExecutorService);
    }

    public @NonNull AuthenticationResponse createAuthentication(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims) {
        Map<String, Object> claims = buildAttributes(providerName, tokenResponse, openIdClaims);
        List<String> roles = getRoles(openIdClaims);
        String username = openIdClaims.getSubject();
        LOG.info("Creating new authentication for user: {}", username);
        return AuthenticationResponse.success(username, roles, claims);
    }

    @Override
    public @NonNull Publisher<AuthenticationResponse> createAuthenticationResponse(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims, @Nullable State state) {
        return Mono.fromCallable(() -> createAuthentication(providerName, tokenResponse, openIdClaims)).subscribeOn(Schedulers.boundedElastic())
                .subscribeOn(ioScheduler);
    }

    /**
     * @param providerName  The OpenID provider name
     * @param tokenResponse The token response
     * @param openIdClaims  The OpenID claims
     * @return The attributes to set in the {@link Authentication}
     */
    protected Map<String, Object> buildAttributes(String providerName, OpenIdTokenResponse tokenResponse, OpenIdClaims openIdClaims) {
        Map<String, Object> claims = new HashMap<>(openIdClaims.getClaims());
        Claims.ALL_CLAIMS.forEach(claims::remove);
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
        memberProfileRepository.findByWorkEmail(openIdClaims.getEmail())
                .ifPresent(memberProfile -> {
                        LOG.info("MemberProfile of the user: {}", memberProfile);
                        roles.addAll(roleRepository.findUserRoles(memberProfile.getId())
                                .stream()
                                .map(Role::getRole)
                                .toList());
                });

        LOG.info("Email address of the user: {}", openIdClaims.getEmail());
        LOG.info("List of roles from roleRepository: {}", roles);
        return roles;
    }
}
