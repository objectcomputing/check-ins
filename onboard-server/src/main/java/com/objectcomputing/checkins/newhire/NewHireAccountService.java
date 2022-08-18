package com.objectcomputing.checkins.newhire;

import com.objectcomputing.checkins.newhire.commons.NewHireAccountConfig;
import com.objectcomputing.checkins.newhire.commons.SharableNewHireAccount;
import com.objectcomputing.checkins.newhire.model.*;
import com.objectcomputing.checkins.commons.AccountState;
import com.objectcomputing.checkins.security.authentication.srp6.Srp6Secrets;
import com.objectcomputing.checkins.security.authentication.srp6.client.Srp6ClientSecretsFactory;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.List;
import java.util.Random;

import static com.objectcomputing.checkins.newhire.model.NewHireAuthorizationCodeEntity.DEFAULT_TIME_TO_LIVE;

@Singleton
public class NewHireAccountService {
    private static final long CODE_LENGTH = 5;

    private final NewHireAccountRepository userAccountRepository;
    private final NewHireCredentialsRepository newHireCredentialsRepository;
    private final Srp6ClientSecretsFactory secretsFactory;
    private final NewHireAuthorizationCodeRepository userAuthorizationCodeRepository;
    private final NewHireAccountCommunicationService accountCommunicationService;

    public NewHireAccountService(NewHireAccountRepository userAccountRepository,
                                 NewHireCredentialsRepository newHireCredentialsRepository,
                                 Srp6ClientSecretsFactory secretsFactory,
                                 NewHireAuthorizationCodeRepository userAuthorizationCodeRepository,
                                 NewHireAccountCommunicationService accountCommunicationService) {

        this.userAccountRepository = userAccountRepository;
        this.newHireCredentialsRepository = newHireCredentialsRepository;
        this.secretsFactory = secretsFactory;
        this.userAuthorizationCodeRepository = userAuthorizationCodeRepository;
        this.accountCommunicationService = accountCommunicationService;
    }

    public Mono<NewHireAccountEntity> createUserAccountWithCredentials(NewHireAccountConfig newHireAccountConfig) {
        return userAccountRepository.save(
                new NewHireAccountEntity( newHireAccountConfig.getEmailAddress(), AccountState.Active, Instant.now()))

                .flatMap(userAccount -> saveUserAccountCredentials(newHireAccountConfig, userAccount)
                            .flatMap(unknown -> Mono.just(userAccount)));
    }

    public Mono<NewHireCredentialsEntity> saveUserAccountCredentials(NewHireAccountConfig newHireAccountConfig, NewHireAccountEntity newHireAccount) {
        return newHireCredentialsRepository.save(
                new NewHireCredentialsEntity(
                        newHireAccount, newHireAccountConfig.getSalt(), newHireAccountConfig.getPrimaryVerifier()));
    }

    public Mono<NewHireAccountEntity> createUserAccountWithoutCredentials(SharableNewHireAccount sharableNewHireAccount) {
        return userAccountRepository.save(createUserAccount(sharableNewHireAccount))
                .flatMap(userAccount -> {
                    return createActivationCodeAndNotification(userAccount)
                            .thenReturn(userAccount);
                });
    }

    private NewHireAccountEntity createUserAccount(SharableNewHireAccount sharableNewHireAccount) {
        return new NewHireAccountEntity(
                sharableNewHireAccount.getEmailAddress(), AccountState.Pending, Instant.now());
    }

    public Mono<Object> createActivationCodeAndNotification(NewHireAccountEntity userAccount) {
        return Mono.just(generateUserAuthorizationCode(userAccount))
                .flatMap(activationCodeInputs -> userAuthorizationCodeRepository.save(activationCodeInputs.getT1())
                        .flatMap(activationCode -> sendUserAccountNotification(userAccount, activationCodeInputs.getT2())));
    }

    private Mono<Object> sendUserAccountNotification(NewHireAccountEntity userAccount, String code) {
        return accountCommunicationService.sendEmail(
                "OCI New Hire Invitation", 
				userAccount.getEmailAddress(),
                "Congratulations! You have been invited to join OCI as a New Hire on the Onboarding Platform. <br />You have 24 hrs to activate your account.  Your code is " + code + ". <br />Please enter your code in the Activation field at <a target=\"_blank\" href=\"https://onboarding.objectcomputing.com\">Onboarding Portal.</a>",
    }

    private Tuple2<NewHireAuthorizationCodeEntity,String> generateUserAuthorizationCode(NewHireAccountEntity userAccount) {
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

    private NewHireAuthorizationCodeEntity createUserAuthorizationCode(NewHireAccountEntity newHireAccount, String salt, String verifier) {
        return new NewHireAuthorizationCodeEntity(newHireAccount, salt, verifier, AuthorizationPurpose.Activation,
                 Instant.now(), DEFAULT_TIME_TO_LIVE);
    }

    // This is used to activate the user's account with credentials.
    public Mono<NewHireAccountEntity> activateUserAccount(NewHireAccountEntity userAccount, NewHireAccountConfig newHireAccountConfig) {
        return saveUserAccountCredentials(newHireAccountConfig, userAccount)
                .flatMap(credentials ->
                        userAuthorizationCodeRepository.consumeAuthorizationCodes(userAccount.getId(), AuthorizationPurpose.Activation))
                .thenReturn(userAccount);
    }
}
