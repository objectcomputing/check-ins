package com.objectcomputing.checkins.auth.operations;

import com.nimbusds.srp6.SRP6ServerSession;
import com.objectcomputing.checkins.commons.AccountState;
import com.objectcomputing.checkins.auth.AuthSettings;
import com.objectcomputing.checkins.security.authentication.srp6.Srp6Challenge;
import com.objectcomputing.checkins.security.authentication.srp6.server.Srp6AuthenticationChallengeFactory;
import com.objectcomputing.checkins.security.authentication.srp6.server.Srp6ServerSessionFactory;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

import io.micronaut.session.Session;

@Singleton
public class ChallengeOperation {
    private static final Logger AUTH_LOG = AuthSettings.AUTH_LOG;

    private final Srp6ServerSessionFactory srp6ServerSessionFactory;
    private final Srp6AuthenticationChallengeFactory srp6AuthenticationChallengeGeneratorFactory;
    private final AuthenticationSessionHandler authenticationSessionHandler;

    public ChallengeOperation(Srp6ServerSessionFactory srp6ServerSessionFactory,
                              Srp6AuthenticationChallengeFactory srp6AuthenticationChallengeGeneratorFactory,
                              AuthenticationSessionHandler authenticationSessionHandler) {

        this.srp6ServerSessionFactory = srp6ServerSessionFactory;
        this.srp6AuthenticationChallengeGeneratorFactory = srp6AuthenticationChallengeGeneratorFactory;
        this.authenticationSessionHandler = authenticationSessionHandler;
    }

    public Mono<Srp6Challenge> challenge(Session session, ChallengeAccount challengeAccount) {
        return Mono.just(challengeAccount)
                .flatMap(challengedAccount -> {
                    if (canBeChallenged(challengedAccount)) {
                        try {
                            return Mono.just(srp6ServerSessionFactory.create())
                                    .flatMap(srp6Session -> generateChallengeResponse(challengedAccount, srp6Session)
                                            .doFinally((st) -> authenticationSessionHandler.initialize(session, srp6Session, challengedAccount)));
                        } catch (Throwable fatalError) {
                            AUTH_LOG.warn("Failed to generate challenge due malformed account ({}}) with error message: {}}",
                                    challengeAccount.getEmailAddress(), fatalError.getMessage());
                        }
                    }
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.defer(() -> generateFakeChallengeResponse(challengeAccount)));
    }

    private Mono<Srp6Challenge> generateChallengeResponse(ChallengeAccount challengedAccount, SRP6ServerSession srp6Session) {
        return Mono.just(srp6AuthenticationChallengeGeneratorFactory.create(
                srp6Session, challengedAccount.getCredentials()));
    }

    private Mono<Srp6Challenge> generateFakeChallengeResponse(ChallengeAccount challengeAccount) {
        return Mono.just(srp6AuthenticationChallengeGeneratorFactory.createFake(
                challengeAccount.getEmailAddress()));
    }

    private boolean canBeChallenged(ChallengeAccount challengedAccount) {
        return AccountState.Active == challengedAccount.getState()
                || AccountState.Pending == challengedAccount.getState();
    }
}