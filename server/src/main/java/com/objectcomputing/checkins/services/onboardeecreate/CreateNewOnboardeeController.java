package com.objectcomputing.checkins.services.onboardeecreate;

import com.objectcomputing.checkins.services.onboardeecreate.newhire.NewHireAccountService;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.commons.SharableNewHireAccount;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.endpoint.SharableNewHireAccountConverter;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountRepository;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import reactor.core.publisher.Mono;

import java.util.Map;


@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/services/create-onboardee")
public class CreateNewOnboardeeController {

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



    @Post("/")
    @Secured(RoleType.Constants.HR_ROLE)
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<Map<String, Object>> createUserAccountWithoutCredentials(@Body SharableNewHireAccount sharableNewHireAccount) {
        return userAccountService.createUserAccountWithoutCredentials(sharableNewHireAccount)
                .flatMap(userAccount -> Mono.just(Map.of("success", Boolean.TRUE)));
    }
}
