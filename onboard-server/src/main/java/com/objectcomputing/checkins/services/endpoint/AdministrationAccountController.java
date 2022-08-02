package com.objectcomputing.checkins.services.endpoint;

import com.objectcomputing.checkins.services.commons.SharableLoginAccount;
import com.objectcomputing.checkins.services.model.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Controller("/platform/api/administration/account")
public class AdministrationAccountController {

    private final LoginAccountRepository loginAccountRepository;
    private final LoginAuthorizationCodeRepository loginAuthorizationCodeRepository;
    private final UserAccountConverter userAccountConverter;

    public AdministrationAccountController(LoginAccountRepository loginAccountRepository,
                                           LoginAuthorizationCodeRepository loginAuthorizationCodeRepository,
                                           UserAccountConverter userAccountConverter) {
        this.loginAccountRepository = loginAccountRepository;
        this.loginAuthorizationCodeRepository = loginAuthorizationCodeRepository;
        this.userAccountConverter = userAccountConverter;
    }

    @Get("/{userAccountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<SharableLoginAccount> get(UUID userAccountId) {
        return loginAccountRepository.findById(userAccountId).flatMap(userAccountConverter::convert);
    }

    @Get("/organization")
    @Produces(MediaType.APPLICATION_JSON)
    public Flux<Organization> getOrganizationsByQuery(@QueryValue("emailAddress") String emailAddress) {
        return loginAccountRepository.findByEmailAddress(emailAddress)
                .map(UserAccount::getOrganization);
    }

    @Get("/{emailAddress}}/organization")
    @Produces(MediaType.APPLICATION_JSON)
    public Flux<Organization> getOrganizations(String emailAddress) {
        return loginAccountRepository.findByEmailAddress(emailAddress)
                .map(UserAccount::getOrganization);
    }

    @Get("/{userAccountId}/authorizations")
    @Produces(MediaType.APPLICATION_JSON)
    public Flux<LoginAuthorizationCode> getAuthorizationCodes(UUID userAccountId) {
        return loginAuthorizationCodeRepository.findAllByUserAccountId(userAccountId);
    }

    @Get("/{userAccountId}/authorizations/active")
    @Produces(MediaType.APPLICATION_JSON)
    public Publisher<LoginAuthorizationCode> getActiveAuthorizationCodes(UUID userAccountId,
                                                                         @QueryValue("purpose") Optional<String> purposeString) {

        if(purposeString.isPresent()) {
            LoginAuthorizationPurpose purpose = LoginAuthorizationPurpose.valueOf(purposeString.get());
            return loginAuthorizationCodeRepository.findActiveUserAuthorizationCodesByUserAccountIdAndPurpose(userAccountId, purpose);
        } else {
            return loginAuthorizationCodeRepository.findAllActiveUserAuthorizationCodesByUserAccountId(userAccountId);
        }
    }

    @Get("/{userAccountId}/authorizations/check")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Boolean> hasActiveActivationAuthorizationCode(UUID userAccountId,
                                                              @QueryValue("purpose") String purposeString) {
        LoginAuthorizationPurpose purpose = LoginAuthorizationPurpose.valueOf(purposeString);

        return loginAuthorizationCodeRepository
                .hasAnActiveUserAuthorizationCodesByUserAccountIdAndPurpose(userAccountId, purpose);
    }
}
