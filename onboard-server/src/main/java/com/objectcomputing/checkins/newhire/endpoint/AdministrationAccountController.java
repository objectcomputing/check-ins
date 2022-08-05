package com.objectcomputing.checkins.newhire.endpoint;

import com.objectcomputing.checkins.newhire.model.LoginAccountRepository;
import com.objectcomputing.checkins.newhire.model.LoginAuthorizationCode;
import com.objectcomputing.checkins.newhire.model.LoginAuthorizationCodeRepository;
import com.objectcomputing.checkins.services.commons.SharableLoginAccount;
import com.objectcomputing.checkins.newhire.model.LoginAuthorizationPurpose;
//import platform.account.model.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Controller("/platform/api/administration/account")
public class AdministrationAccountController {

    private final LoginAccountRepository userAccountRepository;
    private final LoginAuthorizationCodeRepository userAuthorizationCodeRepository;
    private final UserAccountConverter userAccountConverter;

    public AdministrationAccountController(LoginAccountRepository userAccountRepository,
                                           LoginAuthorizationCodeRepository userAuthorizationCodeRepository,
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
