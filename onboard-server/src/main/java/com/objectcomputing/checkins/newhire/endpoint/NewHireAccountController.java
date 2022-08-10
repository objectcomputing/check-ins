package com.objectcomputing.checkins.newhire.endpoint;

import com.objectcomputing.checkins.ErrorCodes;
import com.objectcomputing.checkins.newhire.NewHireAccountService;
import com.objectcomputing.checkins.auth.exceptions.NoRecordFoundError;
import com.objectcomputing.checkins.newhire.commons.SharableNewHireAccount;
import com.objectcomputing.checkins.newhire.commons.NewHireAccountConfig;
import com.objectcomputing.checkins.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.newhire.model.NewHireAccountRepository;
import com.objectcomputing.checkins.security.authentication.AuthenticatedActor;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/auth/api/account")
public class NewHireAccountController {

    private final NewHireAccountRepository userAccountRepository;
    private final NewHireAccountService userAccountService;
    private final SharableNewHireAccountConverter userAccountConverter;

    public NewHireAccountController(
            NewHireAccountRepository userAccountRepository,
            NewHireAccountService userAccountService,
            SharableNewHireAccountConverter userAccountConverter) {

        this.userAccountRepository = userAccountRepository;
        this.userAccountService = userAccountService;
        this.userAccountConverter = userAccountConverter;
    }

    @Get("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<SharableNewHireAccount> get(AuthenticatedActor actor) {
        return userAccountRepository.findByEmailAddress(actor.getEmailAddress())
                .flatMap(userAccountConverter::convert);
    }

    @Post("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Map<String, Object>> createUserAccountWithoutCredentials(@Body SharableNewHireAccount sharableNewHireAccount) {
        return userAccountService.createUserAccountWithoutCredentials(sharableNewHireAccount)
                .flatMap(userAccount -> Mono.just(Map.of("success", Boolean.TRUE)));
    }

    @Put("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Map<String,Object>> update(AuthenticatedActor actor, @Body NewHireAccountConfig newHireAccountConfig) {
        return userAccountRepository.findByEmailAddress(actor.getEmailAddress())
                .flatMap(userAccount -> userAccountService.activateUserAccount(userAccount, newHireAccountConfig))
                .flatMap(userAccount -> Mono.just(Map.of("success", Boolean.TRUE)));
    }

    @Get("/{userAccountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<SharableNewHireAccount> get(AuthenticatedActor actor, UUID userAccountId) {
        return userAccountRepository.findById(userAccountId)
                .flatMap(userAccountConverter::convert)
                .switchIfEmpty(noAccountFound());
    }

    private Mono<SharableNewHireAccount> noAccountFound() {
        return Mono.defer(() -> Mono.error(new NoRecordFoundError(ErrorCodes.NO_ACCOUNT_FOUND, "No account found")));
    }
}
