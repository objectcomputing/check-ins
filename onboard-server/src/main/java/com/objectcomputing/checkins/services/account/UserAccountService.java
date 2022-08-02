package com.objectcomputing.checkins.services.account;

import com.objectcomputing.checkins.services.endpoint.LoginConfig;
import com.objectcomputing.checkins.services.model.*;
import com.objectcomputing.checkins.services.commons.account.AccountRole;
import com.objectcomputing.checkins.services.commons.account.AccountState;
import com.objectcomputing.checkins.security.authentication.srp6.Srp6Secrets;
import com.objectcomputing.checkins.security.authentication.srp6.client.Srp6ClientSecretsFactory;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.List;
import java.util.Random;

import static com.objectcomputing.geoai.platform.account.model.UserAuthorizationCode.DEFAULT_TIME_TO_LIVE;

@Singleton
public class UserAccountService {
    private static final long CODE_LENGTH = 5;

    private final UserAccountRepository userAccountRepository;
    private final LocalUserCredentialsRepository localUserCredentialsRepository;
    private final Srp6ClientSecretsFactory secretsFactory;
    private final UserAuthorizationCodeRepository userAuthorizationCodeRepository;
    private final AccountInitializationServices accountInitializationServices;
    private final AccountCommunicationService accountCommunicationService;

    public UserAccountService(UserAccountRepository userAccountRepository,
                              LocalUserCredentialsRepository localUserCredentialsRepository,
                              Srp6ClientSecretsFactory secretsFactory,
                              UserAuthorizationCodeRepository userAuthorizationCodeRepository,
                              AccountInitializationServices accountInitializationServices,
                              AccountCommunicationService accountCommunicationService) {

        this.userAccountRepository = userAccountRepository;
        this.localUserCredentialsRepository = localUserCredentialsRepository;
        this.secretsFactory = secretsFactory;
        this.userAuthorizationCodeRepository = userAuthorizationCodeRepository;
        this.accountInitializationServices = accountInitializationServices;
        this.accountCommunicationService = accountCommunicationService;
    }

    public Mono<UserAccount> createUserAccountWithCredentials(LoginConfig loginConfig, Organization organization) {
        return userAccountRepository.save(
                new UserAccount(organization, loginConfig.getEmailAddress(), AccountState.Active, AccountRole.OrganizationOwner, Instant.now()))

                .flatMap(userAccount -> saveUserAccountCredentials(loginConfig, userAccount)
                            .flatMap(credentials -> accountInitializationServices.initializeAccount(userAccount, loginConfig))
                            .flatMap(unknown -> Mono.just(userAccount)));
    }

    private Mono<LocalUserCredentials> saveUserAccountCredentials(loginConfig loginConfig, UserAccount userAccount) {
        return localUserCredentialsRepository.save(
                new LocalUserCredentials(
                        userAccount, loginConfig.getSalt(), loginConfig.getPrimaryVerifier()));
    }

    public Mono<Boolean> checkEmailAddressInUse(Organization organization, String emailAddress) {
        return userAccountRepository.isEmailAddressInUse(organization.getId(), emailAddress);
    }

    public Mono<UserAccount> createUserAccountWithoutCredentials(LoginConfig loginConfig, Organization organization) {
        return userAccountRepository.save(createUserAccount(loginConfig, organization))
                .flatMap(userAccount -> {
                    return createActivationCodeAndNotification(userAccount)
                            .thenReturn(userAccount);
                });
    }

    private UserAccount createUserAccount(LoginConfig loginConfig, Organization organization) {
        return new UserAccount(
                organization, loginConfig.getEmailAddress(), AccountState.Pending, AccountRole.OrganizationMember, Instant.now());
    }

    public Mono<Object> createActivationCodeAndNotification(UserAccount userAccount) {
        return Mono.just(generateUserAuthorizationCode(userAccount))
                .flatMap(activationCodeInputs -> userAuthorizationCodeRepository.save(activationCodeInputs.getT1())
                        .flatMap(activationCode -> sendUserAccountNotification(userAccount, activationCodeInputs.getT2())));
    }

    private Mono<Object> sendUserAccountNotification(UserAccount userAccount, String code) {
        return accountCommunicationService.sendEmail(
                "GeoAI Account Invitation", List.of(userAccount.getEmailAddress()),
                "You are invited to join GeoAI workspace " + userAccount.getOrganization().getWorkspace()
                        + ".geoai.app.  You have 15 minutes to activate your account.  Your code is " + code) ;
    }

    private Mono<Object> initializeAccount(LoginConfig loginConfig, UserAccount inviteeUserAccount) {
        return accountInitializationServices.initializeAccount(inviteeUserAccount, loginConfig);
    }

    private Tuple2<LoginAuthorizationCode,String> generateUserAuthorizationCode(UserAccount userAccount) {
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

    private LoginAuthorizationCode createUserAuthorizationCode(UserAccount userAccount, String salt, String verifier) {
        return new LoginAuthorizationCode(userAccount, salt, verifier, LoginAuthorizationPurpose.Activation,
                LoginAuthorizationSource.WorkspaceInvitation, Instant.now(), DEFAULT_TIME_TO_LIVE);
    }

    public Mono<UserAccount> activateUserAccount(UserAccount userAccount, LoginConfig loginConfig) {
        return saveUserAccountCredentials(loginConfig, userAccount)
                .flatMap(credentials ->
                        userAuthorizationCodeRepository.consumeAuthorizationCodes(userAccount.getId(), LoginAuthorizationPurpose.Activation))
                .flatMap(updateCount -> accountInitializationServices.initializeAccount(userAccount, loginConfig))
                .thenReturn(userAccount);
    }
}
