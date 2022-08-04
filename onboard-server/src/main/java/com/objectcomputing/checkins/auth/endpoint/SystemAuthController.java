package com.objectcomputing.checkins.auth.endpoint;

import com.objectcomputing.geoai.core.accessor.AccessorSource;
import com.objectcomputing.geoai.platform.account.model.UserAuthorizationCodeRepository;
import com.objectcomputing.geoai.platform.auth.AuthSettings;
import com.objectcomputing.geoai.platform.auth.commons.AuthenticationRequest;
import com.objectcomputing.geoai.platform.auth.commons.ChallengeRequest;
import com.objectcomputing.geoai.platform.auth.exceptions.AuthenticationError;
import com.objectcomputing.geoai.platform.auth.operations.*;
import com.objectcomputing.geoai.platform.system.model.SystemAccount;
import com.objectcomputing.geoai.platform.system.model.SystemAccountRepository;
import com.objectcomputing.geoai.platform.token.commons.AuthorizationToken;
import com.objectcomputing.geoai.security.authentication.srp6.Srp6Challenge;
import com.objectcomputing.geoai.security.authentication.srp6.Srp6Credentials;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.session.Session;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.Callable;

@Controller("/auth/api/system")
public class SystemAuthController {
    private static final Logger AUTH_LOG = AuthSettings.AUTH_LOG;

    protected static final String DEFAULT_AUTH_FAILURE_MESSAGE = "Authentication failed. Credentials do not match.";

    private final SystemAccountRepository systemAccountRepository;
    private final ChallengeOperation challengeOperation;
    private final AuthenticationOperation authenticationOperation;
    private final AuthSessionHandler authSessionHelper;

    public SystemAuthController(SystemAccountRepository systemAccountRepository,
                                UserAuthorizationCodeRepository userAuthorizationCodeRepository,
                                ChallengeOperation challengeOperation,
                                AuthenticationOperation authenticationOperation,
                                ActivationOperation activationOperation,
                                AuthSessionHandler authSessionHandler) {
        this.systemAccountRepository = systemAccountRepository;
        this.challengeOperation = challengeOperation;
        this.authenticationOperation = authenticationOperation;
        this.authSessionHelper = authSessionHandler;
    }

    @Post("/challenge")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<Srp6Challenge> challenge(Session session, @Body ChallengeRequest challengeRequest) {
        return systemAccountRepository.findByIdentity(challengeRequest.getIdentity())
                .flatMap(this::asChallengeAccount)
                .flatMap(challengeAccount -> challengeOperation.challenge(session, challengeAccount));
    }

    @Post("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<AuthorizationToken> authenticate(Session session, @Body AuthenticationRequest authenticationRequest) {
        return fromOptional(() -> authSessionHelper.getAccessor(session))
                .flatMap(accessor -> systemAccountRepository.findById(accessor.getId()))
                .filter(userAccount -> authenticationRequest.getIdentity().equals(userAccount.getIdentity()))
                .flatMap(this::asAuthenticatableAccount)
                .flatMap(authenticatableAccount -> authenticationOperation
                        .authenticate(session, authenticatableAccount, authenticationRequest.getSecret()))
                .switchIfEmpty(Mono.defer(() -> {
                    AUTH_LOG.warn("No session attributes present for ({})", authenticationRequest.getIdentity());

                    return Mono.error(new AuthenticationError(DEFAULT_AUTH_FAILURE_MESSAGE));
                }))
                .doFinally((st) -> authSessionHelper.cleanup(session));
    }

    private Mono<AuthenticatableAccount> asAuthenticatableAccount(SystemAccount account) {
        return Mono.just(new AuthenticatableAccount(
                account.getId(), AccessorSource.System, account.getIdentity(), account.getState(), account.getRole()));
    }

    private Mono<ChallengeAccount> asChallengeAccount(SystemAccount account) {
        return Mono.just(new ChallengeAccount(
                account.getId(), AccessorSource.System, account.getState(),
                new Srp6Credentials(
                        account.getIdentity(),
                        account.getSalt(),
                        account.getVerifier())));
    }

    private <T> Mono<T> fromOptional(Callable<Optional<T>> callable) {
        return Mono.fromCallable(callable)
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty));
    }
}