package com.objectcomputing.checkins.newhire.endpoint;

import com.objectcomputing.checkins.newhire.commons.SharableNewHireAccount;
import com.objectcomputing.checkins.newhire.model.NewHireAccountRepository;
import com.objectcomputing.checkins.newhire.model.NewHireAuthorizationCodeEntity;
import com.objectcomputing.checkins.newhire.model.NewHireAuthorizationCodeRepository;
import com.objectcomputing.checkins.newhire.model.AuthorizationPurpose;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/platform/api/administration/account")
public class AdministrationNewHireAccountController {

    private final NewHireAccountRepository userAccountRepository;
    private final NewHireAuthorizationCodeRepository userAuthorizationCodeRepository;
    private final SharableNewHireAccountConverter userAccountConverter;

    public AdministrationNewHireAccountController(NewHireAccountRepository userAccountRepository,
                                                  NewHireAuthorizationCodeRepository userAuthorizationCodeRepository,
                                                  SharableNewHireAccountConverter userAccountConverter) {
        this.userAccountRepository = userAccountRepository;
        this.userAuthorizationCodeRepository = userAuthorizationCodeRepository;
        this.userAccountConverter = userAccountConverter;
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<SharableNewHireAccount> get(UUID id) {
        return userAccountRepository.findById(id).flatMap(userAccountConverter::convert);
    }

    @Get("/{id}/authorizations")
    @Produces(MediaType.APPLICATION_JSON)
    public Flux<NewHireAuthorizationCodeEntity> getAuthorizationCodes(UUID id) {
        return userAuthorizationCodeRepository.findAllByNewHireAccountId(id);
    }

    @Get("/{id}/authorizations/active")
    @Produces(MediaType.APPLICATION_JSON)
    public Publisher<NewHireAuthorizationCodeEntity> getActiveAuthorizationCodes(UUID id,
                                                                                 @QueryValue("purpose") Optional<String> purposeString) {

        if(purposeString.isPresent()) {
            AuthorizationPurpose purpose = AuthorizationPurpose.valueOf(purposeString.get());
            return userAuthorizationCodeRepository.findActiveUserAuthorizationCodesByUserAccountIdAndPurpose(id, purpose);
        } else {
            return userAuthorizationCodeRepository.findAllActiveUserAuthorizationCodesByUserAccountId(id);
        }
    }

    @Get("/{userAccountId}/authorizations/check")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Boolean> hasActiveActivationAuthorizationCode(UUID userAccountId,
                                                              @QueryValue("purpose") String purposeString) {
        AuthorizationPurpose purpose = AuthorizationPurpose.valueOf(purposeString);

        return userAuthorizationCodeRepository
                .hasAnActiveUserAuthorizationCodesByUserAccountIdAndPurpose(userAccountId, purpose);
    }
}
