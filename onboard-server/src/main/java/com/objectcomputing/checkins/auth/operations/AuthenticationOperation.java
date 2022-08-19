package com.objectcomputing.checkins.auth.operations;

import com.nimbusds.srp6.SRP6ServerSession;
import com.objectcomputing.checkins.auth.AuthSettings;
import com.objectcomputing.checkins.auth.exceptions.AuthenticationError;
import com.objectcomputing.checkins.commons.AccountState;
import com.objectcomputing.checkins.commons.Identifiable;
import com.objectcomputing.checkins.security.authentication.srp6.server.Srp6CredentialAuthenticator;
import com.objectcomputing.checkins.security.authorization.AuthorizationToken;
import com.objectcomputing.checkins.security.token.AuthorizationTokenBuilder;
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
public class AuthenticationOperation {
    private static final Logger AUTH_LOG = AuthSettings.AUTH_LOG;

    protected static final String DEFAULT_AUTH_FAILURE_MESSAGE = "Authentication failed. Credentials do not match.";

    private final AuthenticationSessionHandler authenticationSessionHandler;
    private final AuthorizationTokenBuilder authorizationTokenBuilder;

    public AuthenticationOperation(AuthenticationSessionHandler authenticationSessionHandler,
                                   AuthorizationTokenBuilder authorizationTokenBuilder) {
        this.authenticationSessionHandler = authenticationSessionHandler;
        this.authorizationTokenBuilder = authorizationTokenBuilder;
    }

    public Mono<AuthorizationToken> authenticate(Session session, AuthenticatableAccount authenticatableAccount, String secret) {

        return getSessionAttributes(session)
                .flatMap(sessionAttributes -> {

                    if (AccountState.Active != authenticatableAccount.getState()) {
                        AUTH_LOG.warn("No active account found for ({})", authenticatableAccount.getEmailAddress());

                        return Mono.error(new AuthenticationError(DEFAULT_AUTH_FAILURE_MESSAGE));
                    }

                    if (StringUtils.isEmpty(secret)) {
                        AUTH_LOG.warn("Invalid secret specified for authorization request of ({})", authenticatableAccount.getEmailAddress());

                        return Mono.error(new AuthenticationError(DEFAULT_AUTH_FAILURE_MESSAGE));
                    }

                    return Mono.just(new Srp6CredentialAuthenticator(sessionAttributes.getT2()))
                            .flatMap(authenticator -> {
                                try {
                                    if (authenticator.authenticate(secret)) {
                                        return Mono.just(authorizationTokenBuilder.build(authenticatableAccount));
                                    }
                                } catch (Throwable fatalError) {
                                    AUTH_LOG.error("Failed to authenticate due malformed identity ({}) with error message: {}",
                                            authenticatableAccount.getEmailAddress(), fatalError.getMessage());
                                }

                                AUTH_LOG.warn("Fatal authorization error.  Missing login credentials for ({})", authenticatableAccount.getEmailAddress());

                                return Mono.error(new AuthenticationError(DEFAULT_AUTH_FAILURE_MESSAGE));
                            });
                }).doOnSuccess(authorizationToken -> {
                    AUTH_LOG.debug("Adding Auth Token to SESSION");
                    authenticationSessionHandler.registerAuthToken(session, authorizationToken);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    AUTH_LOG.error("No session attributes present for ({}).  HINT: Execute the challenge first.", authenticatableAccount.getEmailAddress());

                    return Mono.error(new AuthenticationError(DEFAULT_AUTH_FAILURE_MESSAGE));
                }));
    }

    private Mono<Tuple2<Identifiable, SRP6ServerSession>> getSessionAttributes(Session session) {
        return fromOptional(() -> authenticationSessionHandler.getNewHireIdentifiable(session))
                .flatMap(identifiable ->
                        fromOptional(() -> authenticationSessionHandler.getSrp6Session(session))
                                .flatMap(srp6Session -> Mono.just(Tuples.of(identifiable, srp6Session))));
    }

    private <T> Mono<T> fromOptional(Callable<Optional<T>> callable) {
        return Mono.fromCallable(callable)
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty));
    }
}
