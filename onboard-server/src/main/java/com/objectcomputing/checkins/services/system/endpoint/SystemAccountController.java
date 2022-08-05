package com.objectcomputing.checkins.services.system.endpoint;


import com.objectcomputing.checkins.services.commons.account.AccountRole;
import com.objectcomputing.checkins.services.commons.account.AccountState;
import com.objectcomputing.checkins.services.system.model.SystemAccount;
import com.objectcomputing.checkins.services.system.model.SystemAccountRepository;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Controller("/platform/api/system/account")
public class SystemAccountController {

    private final
    SystemAccountRepository systemAccountRepository;

    public SystemAccountController(SystemAccountRepository systemAccountRepository) {
        this.systemAccountRepository = systemAccountRepository;
    }

    @Post("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Map<String, Object>> register(@Body SystemAccountConfig systemAccountConfig) {
        AccountRole role = systemAccountConfig.getAdministrator() ?
                AccountRole.Administrator(true, true),: AccountRole.Account;

        SystemAccount systemAccount = new SystemAccount(
                systemAccountConfig.getIdentity(), systemAccountConfig.getSalt(), systemAccountConfig.getVerifier(),
                systemAccountConfig.getRequester(), role, AccountState.Active, Instant.now());

        return systemAccountRepository.save(systemAccount)
                .flatMap(account -> Mono.just(Map.of("success", Boolean.TRUE)));
    }
}
