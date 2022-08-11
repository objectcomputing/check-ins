package com.objectcomputing.checkins.auth.endpoint;

import com.objectcomputing.checkins.ErrorCodes;
import com.objectcomputing.checkins.auth.AuthSettings;
import com.objectcomputing.checkins.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.newhire.model.AuthorizationPurpose;
import com.objectcomputing.checkins.commons.AccountState;
import com.objectcomputing.checkins.newhire.NewHireAccountService;
import com.objectcomputing.checkins.newhire.model.NewHireAccountRepository;
import com.objectcomputing.checkins.newhire.model.NewHireAuthorizationCodeRepository;
import com.objectcomputing.checkins.auth.commons.*;
import com.objectcomputing.checkins.auth.exceptions.ActivationError;
import com.objectcomputing.checkins.auth.exceptions.AuthenticationError;
import com.objectcomputing.checkins.auth.operations.*;
import com.objectcomputing.checkins.security.authentication.srp6.Srp6Challenge;
import com.objectcomputing.checkins.security.authentication.srp6.Srp6Credentials;
import com.objectcomputing.checkins.security.authorization.AuthorizationToken;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.session.Session;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.Callable;

@Controller("/api/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
public class Srp6AuthenticationController {
    private static final Logger AUTH_LOG = AuthSettings.AUTH_LOG;

    protected static final String DEFAULT_AUTH_FAILURE_MESSAGE = "Authentication failed. Credentials do not match.";

    private final NewHireAccountRepository userAccountRepository;
    private final NewHireAuthorizationCodeRepository userAuthorizationCodeRepository;
    private final NewHireAccountService newHireAccountService;
    private final ChallengeOperation challengeOperation;
    private final AuthenticationOperation authenticationOperation;
    private final AuthenticationSessionHandler authSessionHelper;
    private final ActivationOperation activationOperation;

    public Srp6AuthenticationController(NewHireAccountRepository userAccountRepository,
                                        NewHireAuthorizationCodeRepository userAuthorizationCodeRepository,
                                        NewHireAccountService newHireAccountService,
                                        ChallengeOperation challengeOperation,
                                        AuthenticationOperation authenticationOperation,
                                        ActivationOperation activationOperation,
                                        AuthenticationSessionHandler authenticationSessionHandler) {
        this.userAccountRepository = userAccountRepository;
        this.userAuthorizationCodeRepository = userAuthorizationCodeRepository;
        this.newHireAccountService = newHireAccountService;
        this.challengeOperation = challengeOperation;
        this.authenticationOperation = authenticationOperation;
        this.activationOperation = activationOperation;
        this.authSessionHelper = authenticationSessionHandler;
    }

    @Post(uris = {"/challenge", "/authenticate/challenge"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<Srp6Challenge> authenticateChallenge(Session session, @Body ChallengeRequest challengeRequest) {
        return userAccountRepository.findByEmailAddress(challengeRequest.getEmailAddress())
                .flatMap(this::asAuthenticationChallengeAccount)
                .flatMap(challengeAccount -> challengeOperation.challenge(session, challengeAccount));
    }

    @Post("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<AuthorizationToken> authenticate(Session session, @Body AuthenticationRequest authenticationRequest) {
        return fromOptional(() -> authSessionHelper.getNewHireIdentifiable(session))
                .flatMap(identifiable -> userAccountRepository.findById(identifiable.getId()))
                .filter(newHire -> authenticationRequest.getEmailAddress().equals(newHire.getEmailAddress()))
                .flatMap(this::asAuthenticatableAccount)
                .flatMap(authenticatableAccount -> authenticationOperation
                        .authenticate(session, authenticatableAccount, authenticationRequest.getSecret()))
                .switchIfEmpty(Mono.defer(() -> {
                    AUTH_LOG.warn("No session attributes present for ({})", authenticationRequest.getEmailAddress());

                    return Mono.error(new AuthenticationError(DEFAULT_AUTH_FAILURE_MESSAGE));
                }))
                .doFinally((st) -> authSessionHelper.cleanup(session));
    }

    @Post("/activate/challenge") // first
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<Srp6Challenge> activateChallenge(Session session, @Body ChallengeRequest challengeRequest) {
        return userAccountRepository.findByEmailAddress(
                        challengeRequest.getEmailAddress())
                .flatMap(this::asActivationChallengeAccount)
                .flatMap(challengeAccount -> challengeOperation.challenge(session, challengeAccount));
    }

    @Post("/activate") // probably enables us to check email & access code, and sets account to ACTIVE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<AuthorizationToken> activate(Session session, @Body ActivationRequest activationRequest) {
        return fromOptional(() -> authSessionHelper.getNewHireIdentifiable(session))
                .flatMap(accessor -> userAccountRepository.findById(accessor.getId()))
                .filter(userAccount -> activationRequest.getEmailAddress().equals(userAccount.getEmailAddress()))
                .flatMap(this::asAuthenticatableAccount)
                .flatMap(authenticatableAccount -> activationOperation
                                .activate(session, authenticatableAccount, activationRequest.getSecret())
                        .flatMap(authorizationToken -> {
                            return userAccountRepository.updateState(authenticatableAccount.getId(), AccountState.Active)
                                    .map(updateCount -> authorizationToken);
                        }))
                .switchIfEmpty(Mono.defer(() -> {
                    AUTH_LOG.warn("No session attributes present for ({})", activationRequest.getEmailAddress());

                    return Mono.error(new AuthenticationError(DEFAULT_AUTH_FAILURE_MESSAGE));
                }))
                .doFinally((st) -> authSessionHelper.cleanup(session));
    }

    @Post("/activate/extend")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<Srp6Challenge> extendActivation(Session session, @Body ChallengeRequest challengeRequest) {
        return userAccountRepository.findByEmailAddress(
                        challengeRequest.getEmailAddress())
                .flatMap(account -> hasInactiveAuthorizations(account)
                        .flatMap(inactivesFound -> {
                            if(inactivesFound) {
                                return newHireAccountService.createActivationCodeAndNotification(account)
                                        .thenReturn(account);
                            }
                            return Mono.error(new ActivationError(ErrorCodes.ACTIVATION_CODE_NOT_FOUND, "no activation code found"));
                }))
                .flatMap(this::asActivationChallengeAccount)
                .flatMap(challengeAccount -> challengeOperation.challenge(session, challengeAccount));
    }

    @Post("/logout")
    public Mono<LogoutResponse> logout(Session session) {
        authSessionHelper.destroyAuthToken(session);
        return Mono.just(LogoutResponse.success());
    }

    private Mono<AuthenticatableAccount> asAuthenticatableAccount(NewHireAccountEntity account) {
        return Mono.just(new AuthenticatableAccount(
                account.getId(), account.getEmailAddress(), account.getState()));
    }

    private Mono<ChallengeAccount> asAuthenticationChallengeAccount(NewHireAccountEntity account) {
        return Mono.just(new ChallengeAccount(
                account.getId(), account.getState(),
                new Srp6Credentials(
                        account.getEmailAddress(),
                        account.getNewHireCredentials().getSalt(),
                        account.getNewHireCredentials().getVerifier())));
    }

    private Mono<ChallengeAccount> asActivationChallengeAccount(NewHireAccountEntity account) {
        return userAuthorizationCodeRepository
                .findActiveUserAuthorizationCodesByUserAccountIdAndPurpose(account.getId(), AuthorizationPurpose.Activation)
                .flatMap(userAuthorizationCode ->
                        Mono.just(new ChallengeAccount(account.getId(), account.getState(),
                                new Srp6Credentials(
                                        account.getEmailAddress(),
                                        userAuthorizationCode.getSalt(),
                                        userAuthorizationCode.getVerifier()))))
                .switchIfEmpty(hasInactiveAuthorizations(account)
                        .flatMap(inactivesFound -> {
                            if(inactivesFound) {
                                return Mono.error(new ActivationError(ErrorCodes.ACTIVATION_LIMIT_EXPIRED, "activation limit expired"));
                            }
                            return Mono.error(new ActivationError(ErrorCodes.ACTIVATION_CODE_NOT_FOUND, "no activation code found"));
                        }));

    }

    private Mono<Boolean> hasInactiveAuthorizations(NewHireAccountEntity account) {
        return userAuthorizationCodeRepository.hasAnInactiveUserAuthorizationCodesByUserAccountIdAndPurpose(account.getId(), AuthorizationPurpose.Activation);
    }

    private <T> Mono<T> fromOptional(Callable<Optional<T>> callable) {
        return Mono.fromCallable(callable)
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty));
    }
}
