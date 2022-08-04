package com.objectcomputing.geoai.platform.account.endpoint;

import com.objectcomputing.geoai.platform.account.commons.SharableUserAccount;
import com.objectcomputing.geoai.platform.account.model.*;
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
    public Mono<SharableUserAccount> get(UUID userAccountId) {
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
    public Flux<UserAuthorizationCode> getAuthorizationCodes(UUID userAccountId) {
        return userAuthorizationCodeRepository.findAllByUserAccountId(userAccountId);
    }

    @Get("/{userAccountId}/authorizations/active")
    @Produces(MediaType.APPLICATION_JSON)
    public Publisher<UserAuthorizationCode> getActiveAuthorizationCodes(UUID userAccountId,
                                                                        @QueryValue("purpose") Optional<String> purposeString) {

        if(purposeString.isPresent()) {
            UserAuthorizationPurpose purpose = UserAuthorizationPurpose.valueOf(purposeString.get());
            return userAuthorizationCodeRepository.findActiveUserAuthorizationCodesByUserAccountIdAndPurpose(userAccountId, purpose);
        } else {
            return userAuthorizationCodeRepository.findAllActiveUserAuthorizationCodesByUserAccountId(userAccountId);
        }
    }

    @Get("/{userAccountId}/authorizations/check")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Boolean> hasActiveActivationAuthorizationCode(UUID userAccountId,
                                                              @QueryValue("purpose") String purposeString) {
        UserAuthorizationPurpose purpose = UserAuthorizationPurpose.valueOf(purposeString);

        return userAuthorizationCodeRepository
                .hasAnActiveUserAuthorizationCodesByUserAccountIdAndPurpose(userAccountId, purpose);
    }
}
