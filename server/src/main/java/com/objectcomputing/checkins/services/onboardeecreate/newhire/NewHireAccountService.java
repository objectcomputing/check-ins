package com.objectcomputing.checkins.services.onboardeecreate.newhire;

import com.objectcomputing.checkins.services.email.EmailServices;
import com.objectcomputing.checkins.services.onboardeecreate.commons.AccountState;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.commons.SharableNewHireAccount;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.*;
import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.Srp6Secrets;
import com.objectcomputing.checkins.services.onboardeecreate.security.authentication.srp6.client.Srp6ClientSecretsFactory;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.Random;

import static com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAuthorizationCodeEntity.DEFAULT_TIME_TO_LIVE;

@Singleton
public class NewHireAccountService {
    private static final long CODE_LENGTH = 5;

    private final NewHireAccountRepository userAccountRepository;
    private final Srp6ClientSecretsFactory secretsFactory;
    private final NewHireAuthorizationCodeRepository userAuthorizationCodeRepository;
    private final EmailServices emailServices;

    public NewHireAccountService(NewHireAccountRepository userAccountRepository,
                                 Srp6ClientSecretsFactory secretsFactory,
                                 NewHireAuthorizationCodeRepository userAuthorizationCodeRepository,
                                 EmailServices emailServices) {

        this.userAccountRepository = userAccountRepository;
        this.secretsFactory = secretsFactory;
        this.userAuthorizationCodeRepository = userAuthorizationCodeRepository;
        this.emailServices = emailServices;
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
        return Mono.just(emailServices.sendAndSaveEmail(
                "OCI New Hire Invitation",
                "Congratulations! You have been invited to join OCI as a New Hire on the Onboarding Platform. <br />You have 24 hrs to activate your account.  Your code is " + code + ". <br />Please enter your code in the Activation field at <a target=\"_blank\" href=\"https://onboarding.objectcomputing.com\">Onboarding Portal.</a>",
                true,
                userAccount.getEmailAddress()
        )).map(emailAddresses -> Mono.just(new Object()));
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
}
