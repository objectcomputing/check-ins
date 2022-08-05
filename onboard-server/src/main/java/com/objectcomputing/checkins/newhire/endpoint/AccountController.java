package com.objectcomputing.checkins.newhire.endpoint;

import com.objectcomputing.checkins.auth.LoginAccountService;
import com.objectcomputing.checkins.auth.exceptions.NoRecordFoundError;
import com.objectcomputing.checkins.newhire.model.LoginAccount;
import com.objectcomputing.checkins.newhire.model.LoginAccountRepository;
import com.objectcomputing.checkins.security.authentication.AuthenticatedActor;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Controller("/platform/api/account")
public class AccountController {

    private final LoginAccountRepository userAccountRepository;
    private final LoginAccountService userAccountService;
    private final UserAccountConverter userAccountConverter;

    public AccountController(
            LoginAccountRepository userAccountRepository,
            LoginAccountService userAccountService,
            UserAccountConverter userAccountConverter) {

        this.userAccountRepository = userAccountRepository;
        this.userAccountService = userAccountService;
        this.userAccountConverter = userAccountConverter;
    }

    @Get("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<LoginAccount> get(AuthenticatedActor actor) {
        return userAccountRepository.findByEmailAddress(actor.getIdentity())
                .flatMap(userAccount -> userAccountConverter.convert(userAccount));
    }

    @Put("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Map<String,Object>> update(AuthenticatedActor actor, @Body UserAccountConfig userAccountConfig) {
        return userAccountRepository.findByEmailAddress(actor.getIdentity())
                .flatMap(userAccount -> userAccountService.activateUserAccount(userAccount, userAccountConfig))
                .flatMap(userAccount -> Mono.just(Map.of("success", Boolean.TRUE)));
    }

    @Get("/{userAccountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<LoginAccount> get(AuthenticatedActor actor, UUID userAccountId) {
        return userAccountRepository.findById(userAccountId)
                .flatMap(userAccountConverter::convert)
                .switchIfEmpty(noAccountFound());
    }

    private Mono<LoginAccount> noAccountFound() {
        return Mono.defer(() -> Mono.error(new NoRecordFoundError(PlatformErrorCodes.NO_ACCOUNT_FOUND, "No account found")));
    }
}
