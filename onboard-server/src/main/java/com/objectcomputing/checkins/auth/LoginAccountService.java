package com.objectcomputing.checkins.auth;

import com.objectcomputing.checkins.newhire.endpoint.UserAccountConfig;
import com.objectcomputing.checkins.newhire.model.*;
import com.objectcomputing.checkins.newhire.AccountCommunicationService;
import com.objectcomputing.checkins.services.account.AccountInitializationServices;
import com.objectcomputing.checkins.newhire.model.AccountRole;
import com.objectcomputing.checkins.newhire.model.AccountState;
import com.objectcomputing.checkins.security.authentication.srp6.Srp6Secrets;
import com.objectcomputing.checkins.security.authentication.srp6.client.Srp6ClientSecretsFactory;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.List;
import java.util.Random;

import static com.objectcomputing.checkins.newhire.model.LoginAuthorizationCode.DEFAULT_TIME_TO_LIVE;

@Singleton
public class LoginAccountService {
    private static final long CODE_LENGTH = 5;

    private final LoginAccountRepository userAccountRepository;
    private final LocalUserCredentialsRepository localUserCredentialsRepository;
    private final Srp6ClientSecretsFactory secretsFactory;
    private final LoginAuthorizationCodeRepository userAuthorizationCodeRepository;
    private final AccountInitializationServices accountInitializationServices;
    private final AccountCommunicationService accountCommunicationService;

    public LoginAccountService(LoginAccountRepository userAccountRepository,
                               LocalUserCredentialsRepository localUserCredentialsRepository,
                               Srp6ClientSecretsFactory secretsFactory,
                               LoginAuthorizationCodeRepository userAuthorizationCodeRepository,
                               AccountInitializationServices accountInitializationServices,
                               AccountCommunicationService accountCommunicationService) {

        this.userAccountRepository = userAccountRepository;
        this.localUserCredentialsRepository = localUserCredentialsRepository;
        this.secretsFactory = secretsFactory;
        this.userAuthorizationCodeRepository = userAuthorizationCodeRepository;
        this.accountInitializationServices = accountInitializationServices;
        this.accountCommunicationService = accountCommunicationService;
    }

    public Mono<LoginAccount> createUserAccountWithCredentials(UserAccountConfig userAccountConfig) {
        return userAccountRepository.save(
                new LoginAccount( userAccountConfig.getEmailAddress(), AccountState.Active, AccountRole.Account, Instant.now()))

                .flatMap(userAccount -> saveUserAccountCredentials(userAccountConfig, userAccount)
                            .flatMap(credentials -> accountInitializationServices.initializeAccount(userAccount, userAccountConfig))
                            .flatMap(unknown -> Mono.just(userAccount)));
    }

    private Mono<LocalUserCredentials> saveUserAccountCredentials(UserAccountConfig userAccountConfig, LoginAccount userAccount) {
        return localUserCredentialsRepository.save(
                new LocalUserCredentials(
                        userAccount, userAccountConfig.getSalt(), userAccountConfig.getPrimaryVerifier()));
    }

    public Mono<LoginAccount> createUserAccountWithoutCredentials(UserAccountConfig userAccountConfig) {
        return userAccountRepository.save(createUserAccount(userAccountConfig))
                .flatMap(userAccount -> {
                    return createActivationCodeAndNotification(userAccount)
                            .thenReturn(userAccount);
                });
    }

    private LoginAccount createUserAccount(UserAccountConfig userAccountConfig) {
        return new LoginAccount(
                userAccountConfig.getEmailAddress(), AccountState.Pending, AccountRole.Account, Instant.now());
    }

    public Mono<Object> createActivationCodeAndNotification(LoginAccount userAccount) {
        return Mono.just(generateUserAuthorizationCode(userAccount))
                .flatMap(activationCodeInputs -> userAuthorizationCodeRepository.save(activationCodeInputs.getT1())
                        .flatMap(activationCode -> sendUserAccountNotification(userAccount, activationCodeInputs.getT2())));
    }

    private Mono<Object> sendUserAccountNotification(LoginAccount userAccount, String code) {
        return accountCommunicationService.sendEmail(
                "GeoAI Account Invitation", List.of(userAccount.getEmailAddress()),
                "You are invited to join GeoAI workspace " + userAccount
                        + ".geoai.app.  You have 15 minutes to activate your account.  Your code is " + code) ;
    }

    private Mono<Object> initializeAccount(UserAccountConfig userAccountConfig, LoginAccount inviteeUserAccount) {
        return accountInitializationServices.initializeAccount(inviteeUserAccount, userAccountConfig);
    }

    private Tuple2<LoginAuthorizationCode,String> generateUserAuthorizationCode(LoginAccount userAccount) {
        final String code = generateActivationCode();

        final Srp6Secrets secrets = secretsFactory.generateSecrets(userAccount.getEmailAddress(), code);

        return Tuples.of(createUserAuthorizationCode(userAccount, secrets.getSalt(), secrets.getVerifier()), code);
    }

    private String generateActivationCode() {
        int leftLimit = 'A'; //97; // letter 'a'
        int rightLimit = 'Z'; //122; // letter 'z'

        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(CODE_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private LoginAuthorizationCode createUserAuthorizationCode(LoginAccount loginAccount, String salt, String verifier) {
        return new LoginAuthorizationCode(loginAccount, salt, verifier,LoginAuthorizationPurpose.Activation,
                 Instant.now(), DEFAULT_TIME_TO_LIVE);
    }

    public Mono<LoginAccount> activateUserAccount(LoginAccount userAccount, UserAccountConfig userAccountConfig) {
        return saveUserAccountCredentials(userAccountConfig, userAccount)
                .flatMap(credentials ->
                        userAuthorizationCodeRepository.consumeAuthorizationCodes(userAccount.getId(), LoginAuthorizationPurpose.Activation))
                .flatMap(updateCount -> accountInitializationServices.initializeAccount(userAccount, userAccountConfig))
                .thenReturn(userAccount);
    }
}
