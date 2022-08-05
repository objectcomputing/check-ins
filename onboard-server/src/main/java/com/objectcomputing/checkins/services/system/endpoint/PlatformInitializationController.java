package com.objectcomputing.checkins.services.system.endpoint;

import com.objectcomputing.checkins.services.system.model.SystemAccount;
import com.objectcomputing.checkins.services.system.model.SystemAccountRepository;
import com.objectcomputing.geoai.core.account.AccountRole;
import com.objectcomputing.geoai.core.account.AccountState;
import com.objectcomputing.geoai.platform.system.model.SystemAccount;
import com.objectcomputing.geoai.platform.system.model.SystemAccountRepository;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Controller("/platform/api/system/initialize")
public class PlatformInitializationController {

    private final SystemAccountRepository systemAccountRepository;

    public PlatformInitializationController(SystemAccountRepository systemAccountRepository) {
        this.systemAccountRepository = systemAccountRepository;
    }

    @Post("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Map<String, Object>> initialize(@Body InitializationConfig config) {

        if(systemAccountRepository.hasAdministratorAccountBeenCreated()) {
            return Mono.just(Map.of("success", Boolean.FALSE));
        }

        return systemAccountRepository.save(
                new SystemAccount(
                        config.getSystemOwner().getIdentity(), config.getSystemOwner().getSalt(), config.getSystemOwner().getVerifier(),
                        config.getRequester(), AccountRole.PlatformSuperAdministrator, AccountState.Active, Instant.now()))
                .flatMap(account -> Mono.just(Map.of("success", Boolean.TRUE)));
    }
}