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

    private final UserAccountRepository userAccountRepository;
    private final UserAuthorizationCodeRepository userAuthorizationCodeRepository;
    private final UserAccountConverter userAccountConverter;

    public AdministrationAccountController(UserAccountRepository userAccountRepository,
                                           UserAuthorizationCodeRepository userAuthorizationCodeRepository,
                                           UserAccountConverter userAccountConverter) {
        this.userAccountRepository = userAccountRepository;
        this.userAuthorizationCodeRepository = userAuthorizationCodeRepository;
        this.userAccountConverter = userAccountConverter;
    }

    @Get("/{userAccountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<SharableLoginAccount> get(UUID userAccountId) {
        return userAccountRepository.findById(userAccountId).flatMap(userAccountConverter::convert);
    }

    @Get("/organization")
    @Produces(MediaType.APPLICATION_JSON)
    public Flux<Organization> getOrganizationsByQuery(@QueryValue("emailAddress") String emailAddress) {
        return userAccountRepository.findByEmailAddress(emailAddress)
                .map(UserAccount::getOrganization);
    }

    @Get("/{emailAddress}}/organization")
    @Produces(MediaType.APPLICATION_JSON)
    public Flux<Organization> getOrganizations(String emailAddress) {
        return userAccountRepository.findByEmailAddress(emailAddress)
                .map(UserAccount::getOrganization);
    }

    @Get("/{userAccountId}/authorizations")
    @Produces(MediaType.APPLICATION_JSON)
    public Flux<LoginAuthorizationCode> getAuthorizationCodes(UUID userAccountId) {
        return userAuthorizationCodeRepository.findAllByUserAccountId(userAccountId);
    }

    @Get("/{userAccountId}/authorizations/active")
    @Produces(MediaType.APPLICATION_JSON)
    public Publisher<LoginAuthorizationCode> getActiveAuthorizationCodes(UUID userAccountId,
                                                                         @QueryValue("purpose") Optional<String> purposeString) {

        if(purposeString.isPresent()) {
            LoginAuthorizationPurpose purpose = LoginAuthorizationPurpose.valueOf(purposeString.get());
            return userAuthorizationCodeRepository.findActiveUserAuthorizationCodesByUserAccountIdAndPurpose(userAccountId, purpose);
        } else {
            return userAuthorizationCodeRepository.findAllActiveUserAuthorizationCodesByUserAccountId(userAccountId);
        }
    }

    @Get("/{userAccountId}/authorizations/check")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Boolean> hasActiveActivationAuthorizationCode(UUID userAccountId,
                                                              @QueryValue("purpose") String purposeString) {
        LoginAuthorizationPurpose purpose = LoginAuthorizationPurpose.valueOf(purposeString);

        return userAuthorizationCodeRepository
                .hasAnActiveUserAuthorizationCodesByUserAccountIdAndPurpose(userAccountId, purpose);
    }
}
