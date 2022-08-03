package com.objectcomputing.checkins.auth.endpoint;

import com.objectcomputing.checkins.services.commons.accessor.AccessorSource;
import com.objectcomputing.checkins.services.commons.account.AccountState;
import com.objectcomputing.checkins.services.account.UserAccountService;
import com.objectcomputing.geoai.platform.account.model.*;
import com.objectcomputing.geoai.platform.auth.AuthErrorCodes;
import com.objectcomputing.geoai.platform.auth.AuthSettings;
import com.objectcomputing.geoai.platform.auth.commons.*;
import com.objectcomputing.geoai.platform.auth.exceptions.ActivationError;
import com.objectcomputing.geoai.platform.auth.exceptions.AuthenticationError;
import com.objectcomputing.geoai.platform.auth.operations.*;
import com.objectcomputing.geoai.platform.token.commons.AuthorizationToken;
import com.objectcomputing.checkins.security.authentication.srp6.Srp6Challenge;
import com.objectcomputing.checkins.security.authentication.srp6.Srp6Credentials;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.session.Session;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.Callable;

@Controller("/auth/api")
public class UserAuthController {
    private static final Logger AUTH_LOG = AuthSettings.AUTH_LOG;

    protected static final String DEFAULT_AUTH_FAILURE_MESSAGE = "Authentication failed. Credentials do not match.";

    private final UserAccountRepository userAccountRepository;
    private final UserAuthorizationCodeRepository userAuthorizationCodeRepository;
    private final UserAccountService userAccountService;
    private final ChallengeOperation challengeOperation;
    private final AuthenticationOperation authenticationOperation;
    private final AuthSessionHandler authSessionHelper;
    private final ActivationOperation activationOperation;

    public UserAuthController(UserAccountRepository userAccountRepository,
                              UserAuthorizationCodeRepository userAuthorizationCodeRepository,
                              UserAccountService userAccountService,
                              ChallengeOperation challengeOperation,
                              AuthenticationOperation authenticationOperation,
                              ActivationOperation activationOperation,
                              AuthSessionHandler authSessionHandler) {
        this.userAccountRepository = userAccountRepository;
        this.userAuthorizationCodeRepository = userAuthorizationCodeRepository;
        this.userAccountService = userAccountService;
        this.challengeOperation = challengeOperation;
        this.authenticationOperation = authenticationOperation;
        this.activationOperation = activationOperation;
        this.authSessionHelper = authSessionHandler;
    }

    @Post(uris = {"/challenge", "/authenticate/challenge"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<Srp6Challenge> authenticateChallenge(Session session, @Body ChallengeRequest challengeRequest) {
        return userAccountRepository.findByEmailAddressWithLocalCredentials(
                challengeRequest.getScope(), challengeRequest.getIdentity())
                .flatMap(this::asAuthenticationChallengeAccount)
                .flatMap(challengeAccount -> challengeOperation.challenge(session, challengeAccount));
    }

    @Post("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<AuthorizationToken> authenticate(Session session, @Body AuthenticationRequest authenticationRequest) {
        return fromOptional(() -> authSessionHelper.getAccessor(session))
                .flatMap(accessor -> userAccountRepository.findById(accessor.getId()))
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

    @Post("/activate/challenge")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<Srp6Challenge> activateChallenge(Session session, @Body ChallengeRequest challengeRequest) {
        return userAccountRepository.findByEmailAddressWithLocalCredentials(
                        challengeRequest.getScope(), challengeRequest.getIdentity())
                .flatMap(this::asActivationChallengeAccount)
                .flatMap(challengeAccount -> challengeOperation.challenge(session, challengeAccount));
    }

    @Post("/activate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<AuthorizationToken> activate(Session session, @Body ActivationRequest activationRequest) {
        return fromOptional(() -> authSessionHelper.getAccessor(session))
                .flatMap(accessor -> userAccountRepository.findById(accessor.getId()))
                .filter(userAccount -> activationRequest.getIdentity().equals(userAccount.getIdentity()))
                .flatMap(this::asAuthenticatableAccount)
                .flatMap(authenticatableAccount -> activationOperation
                                .activate(session, authenticatableAccount, activationRequest.getSecret())
                        .flatMap(authorizationToken -> {
                            return userAccountRepository.updateState(authenticatableAccount.getId(), AccountState.Active)
                                    .map(updateCount -> authorizationToken);
                        }))
                .switchIfEmpty(Mono.defer(() -> {
                    AUTH_LOG.warn("No session attributes present for ({})", activationRequest.getIdentity());

                    return Mono.error(new AuthenticationError(DEFAULT_AUTH_FAILURE_MESSAGE));
                }))
                .doFinally((st) -> authSessionHelper.cleanup(session));
    }

    @Post("/activate/extend")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public Mono<Srp6Challenge> extendActivation(Session session, @Body ChallengeRequest challengeRequest) {
        return userAccountRepository.findByEmailAddressWithLocalCredentials(
                        challengeRequest.getScope(), challengeRequest.getIdentity())
                .flatMap(account -> hasInactiveAuthorizations(account)
                        .flatMap(inactivesFound -> {
                            if(inactivesFound) {
                                return userAccountService.createActivationCodeAndNotification(account)
                                        .thenReturn(account);
                            }
                            return Mono.error(new ActivationError(AuthErrorCodes.ACTIVATION_CODE_NOT_FOUND, "no activation code found"));
                }))
                .flatMap(this::asActivationChallengeAccount)
                .flatMap(challengeAccount -> challengeOperation.challenge(session, challengeAccount));
    }

    @Post("/logout")
    public Mono<LogoutResponse> logout(Session session) {
        authSessionHelper.destroyAuthToken(session);
        return Mono.just(LogoutResponse.success());
    }

    private Mono<AuthenticatableAccount> asAuthenticatableAccount(UserAccount account) {
        return Mono.just(new AuthenticatableAccount(
                account.getId(), AccessorSource.User, account.getIdentity(), account.getState(), account.getRole()));
    }

    private Mono<ChallengeAccount> asAuthenticationChallengeAccount(UserAccount account) {
        return Mono.just(new ChallengeAccount(
                account.getId(), AccessorSource.User, account.getState(),
                new Srp6Credentials(
                        account.getIdentity(),
                        account.getLocalUserCredentials().getSalt(),
                        account.getLocalUserCredentials().getPrimaryVerifier())));
    }

    private Mono<ChallengeAccount> asActivationChallengeAccount(UserAccount account) {
        return userAuthorizationCodeRepository
                .findActiveUserAuthorizationCodesByUserAccountIdAndPurpose(account.getId(), UserAuthorizationPurpose.Activation)
                .flatMap(userAuthorizationCode ->
                        Mono.just(new ChallengeAccount(account.getId(), AccessorSource.User, account.getState(),
                                new Srp6Credentials(
                                        account.getIdentity(),
                                        userAuthorizationCode.getSalt(),
                                        userAuthorizationCode.getVerifier()))))
                .switchIfEmpty(hasInactiveAuthorizations(account)
                        .flatMap(inactivesFound -> {
                            if(inactivesFound) {
                                return Mono.error(new ActivationError(AuthErrorCodes.ACTIVATION_LIMIT_EXPIRED, "activation limit expired"));
                            }
                            return Mono.error(new ActivationError(AuthErrorCodes.ACTIVATION_CODE_NOT_FOUND, "no activation code found"));
                        }));

    }

    private Mono<Boolean> hasInactiveAuthorizations(UserAccount account) {
        return userAuthorizationCodeRepository.hasAnInactiveUserAuthorizationCodesByUserAccountIdAndPurpose(account.getId(), UserAuthorizationPurpose.Activation);
    }

    private <T> Mono<T> fromOptional(Callable<Optional<T>> callable) {
        return Mono.fromCallable(callable)
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty));
    }
}
