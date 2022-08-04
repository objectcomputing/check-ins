package com.objectcomputing.geoai.platform.account.endpoint;

import com.objectcomputing.geoai.platform.PlatformErrorCodes;
import com.objectcomputing.geoai.platform.account.AuthorizedProductService;
import com.objectcomputing.geoai.platform.account.UserAccountService;
import com.objectcomputing.geoai.platform.account.commons.AuthorizedProduct;
import com.objectcomputing.geoai.platform.account.commons.SharableUserAccount;
import com.objectcomputing.geoai.platform.account.model.Organization;
import com.objectcomputing.geoai.platform.account.model.UserAccount;
import com.objectcomputing.geoai.platform.account.model.UserAccountRepository;
import com.objectcomputing.geoai.platform.exceptions.NoRecordFoundError;
import com.objectcomputing.geoai.platform.security.authentication.PlatformAuthenticatedActor;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Controller("/platform/api/account")
public class AccountController {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountService userAccountService;
    private final UserAccountConverter userAccountConverter;
    private final AuthorizedProductService authorizedProductService;

    public AccountController(
            UserAccountRepository userAccountRepository,
            UserAccountService userAccountService,
            UserAccountConverter userAccountConverter,
            AuthorizedProductService authorizedProductService) {

        this.userAccountRepository = userAccountRepository;
        this.userAccountService = userAccountService;
        this.userAccountConverter = userAccountConverter;
        this.authorizedProductService = authorizedProductService;
    }

    @Get("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<SharableUserAccount> get(PlatformAuthenticatedActor<UserAccount> actor) {
        return userAccountConverter.convert(actor.getAccount());
    }

    @Put("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Map<String,Object>> update(PlatformAuthenticatedActor<UserAccount> actor, @Body UserAccountConfig userAccountConfig) {
        return userAccountService.activateUserAccount(actor.getAccount(), userAccountConfig)
                .flatMap(userAccount -> Mono.just(Map.of("success", Boolean.TRUE)));
    }

    @Get("/{userAccountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<SharableUserAccount> get(PlatformAuthenticatedActor<UserAccount> actor, UUID userAccountId) {
        return userAccountRepository.findById(userAccountId)
                .filter(userAccount ->
                        haveSameOrganizations(userAccount.getOrganization(), actor.getAccount().getOrganization()))
                .flatMap(userAccountConverter::convert)
                .switchIfEmpty(noAccountFound());
    }

    private Mono<SharableUserAccount> noAccountFound() {
        return Mono.defer(() -> Mono.error(new NoRecordFoundError(PlatformErrorCodes.NO_ACCOUNT_FOUND, "No account found")));
    }

    private boolean haveSameOrganizations(Organization o1, Organization o2) {
        return o1.getId().equals(o2.getId());
    }

    @Get("/authorized-product")
    @Produces(MediaType.APPLICATION_JSON)
    public Flux<AuthorizedProduct> getAuthorizedProducts(PlatformAuthenticatedActor<UserAccount> actor) {
        return authorizedProductService.getAuthorizedProducts(actor.getAccount());
    }
}
