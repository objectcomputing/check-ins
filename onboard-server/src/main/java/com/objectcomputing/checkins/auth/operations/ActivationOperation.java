package com.objectcomputing.checkins.auth.operations;

import com.nimbusds.srp6.SRP6ServerSession;
import com.objectcomputing.geoai.core.accessor.Accessor;
import com.objectcomputing.geoai.core.account.AccountState;
import com.objectcomputing.geoai.platform.auth.AuthErrorCodes;
import com.objectcomputing.geoai.platform.auth.AuthSettings;
import com.objectcomputing.geoai.platform.auth.exceptions.ActivationError;
import com.objectcomputing.geoai.platform.token.TokenService;
import com.objectcomputing.geoai.platform.token.commons.AuthorizationToken;
import com.objectcomputing.geoai.platform.token.commons.AuthorizationTokenBuilder;
import com.objectcomputing.geoai.security.authentication.srp6.server.Srp6CredentialAuthenticator;
import io.micronaut.core.util.StringUtils;
import io.micronaut.session.Session;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Optional;
import java.util.concurrent.Callable;

@Singleton
public class ActivationOperation {
    private static final Logger AUTH_LOG = AuthSettings.AUTH_LOG;

    protected static final String DEFAULT_AUTH_FAILURE_MESSAGE = "Activation failed. Credentials do not match.";

    private final AuthSessionHandler authSessionHandler;
    private final TokenService tokenService;
    private final AuthorizationTokenBuilder authorizationTokenBuilder;

    public ActivationOperation(AuthSessionHandler authSessionHandler,
                               TokenService tokenService,
                               AuthorizationTokenBuilder authorizationTokenBuilder) {
        this.authSessionHandler = authSessionHandler;
        this.tokenService = tokenService;
        this.authorizationTokenBuilder = authorizationTokenBuilder;
    }

    public Mono<AuthorizationToken> activate(Session session, AuthenticatableAccount authenticatableAccount, String secret) {

        return getSessionAttributes(session)
                .flatMap(sessionAttributes -> {

                    if (AccountState.Pending != authenticatableAccount.getState()) {
                        AUTH_LOG.warn("No active account found for ({})", authenticatableAccount.getIdentity());

                        return Mono.error(new ActivationError(AuthErrorCodes.NO_ACCOUNT_FOUND, DEFAULT_AUTH_FAILURE_MESSAGE));
                    }

                    if (StringUtils.isEmpty(secret)) {
                        AUTH_LOG.warn("Invalid secret specified for activation request of ({})", authenticatableAccount.getIdentity());

                        return Mono.error(new ActivationError(AuthErrorCodes.INVALID_CODE_SECRETS, DEFAULT_AUTH_FAILURE_MESSAGE));
                    }

                    return Mono.just(new Srp6CredentialAuthenticator(sessionAttributes.getT2()))
                            .flatMap(authenticator -> {
                                try {
                                    if (authenticator.authenticate(secret)) {
                                        return tokenService.getOrCreateTokenAuthorizationFor(authenticatableAccount)
                                                .flatMap(tokenAuthorization ->
                                                        Mono.just(authorizationTokenBuilder.build(authenticatableAccount, tokenAuthorization)));
                                    }
                                } catch (Throwable fatalError) {
                                    AUTH_LOG.error("Failed to authenticate due malformed identity ({}) with error message: {}",
                                            authenticatableAccount.getIdentity(), fatalError.getMessage());

                                }

                                AUTH_LOG.warn("Fatal activation error.  Missing login credentials for ({})", authenticatableAccount.getIdentity());

                                return Mono.error(new ActivationError(AuthErrorCodes.UNKNOWN_ERROR, DEFAULT_AUTH_FAILURE_MESSAGE));
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    AUTH_LOG.error("No session attributes present for ({}).  HINT: Execute the challenge first.", authenticatableAccount.getIdentity());

                    return Mono.error(new ActivationError(AuthErrorCodes.UNKNOWN_ERROR, DEFAULT_AUTH_FAILURE_MESSAGE));
                }));
    }

    private Mono<Tuple2<Accessor, SRP6ServerSession>> getSessionAttributes(Session session) {
        return fromOptional(() -> authSessionHandler.getAccessor(session))
                .flatMap(accessor ->
                        fromOptional(() -> authSessionHandler.getSrp6Session(session))
                                .flatMap(srp6Session -> Mono.just(Tuples.of(accessor, srp6Session))));
    }

    private <T> Mono<T> fromOptional(Callable<Optional<T>> callable) {
        return Mono.fromCallable(callable)
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty));
    }
}
